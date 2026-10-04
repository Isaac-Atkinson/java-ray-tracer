package com.example.raytracer.render;

import com.example.raytracer.helper.*;
import com.example.raytracer.geometry.SceneObject;
import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleConsumer;
import java.util.stream.IntStream;

/**
 * This class is responsible for rendering the scene.
 */
public class Renderer {

    public static final double EPSILON = 1e-6;

    //The background colour
    private static final Color backgroundColor = Color.color(0.0, 0.0, 0.0);

    private final Camera camera;

    //The scene to be rendered
    private final RenderScene renderScene;

    //The JavaFX image
    private WritableImage image;
    //The image writer
    private PixelWriter pixelWriter;

    //The shadow sample count
    private int sampleCount = 1;



    public Renderer(WritableImage image, Camera camera, RenderScene renderScene) {
        this.image = image;
        this.camera = camera;
        this.renderScene = renderScene;
        pixelWriter = image.getPixelWriter();
    }




    /**
     * Loops through each pixel, traces a ray and computes the colour
     * for that pixel before writing the colour to the image.
     */
    public void render(DoubleConsumer progressCallback) {

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        Color[][] colors = new Color[height][width];

        AtomicInteger rowsCompleted = new AtomicInteger(0);

        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < image.getWidth(); x++) {

                //Generate a ray
                Ray ray = generateRay(x, y);

                //Find the closest intersection
                Intersection obj = renderScene.closestHit(ray);

                //compute pixel colour
                colors[y][x] = (obj.hit != null)
                        ? applyShading(obj, ray)
                        : backgroundColor;

            }

            int count = rowsCompleted.incrementAndGet();

            progressCallback.accept((double) count / height);
        });

        //write accumulated colours to the image
        Platform.runLater(() -> {
            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    pixelWriter.setColor(x, y, colors[y][x]);
                }
            }
        });
    }

    public void render(){
        render(progressCallback -> {});
    }





    /**
     * Generates a ray in the direction of the pixel.
     * @param x the pixel x coordinate
     * @param y the pixel y coordinate
     * @return the generated ray
     */
    private Ray generateRay(int x, int y){

        double scale = Math.tan(Math.toRadians(camera.getFov() / 2.0));

        double pixelX = (2.0 * ((x + 0.5) / image.getWidth()) - 1)
                * scale;

        double pixelY = (1 - 2.0 * ((y + 0.5) / image.getHeight()))
                * scale;

        Vector direction =
                camera.getForward()
                .add(camera.getRight().mul(pixelX))
                .add(camera.getUp().mul(pixelY));
        direction.normalise();

        return new Ray(camera.getPosition(), direction);
    }



    /**
     * Applies shading at an intersection point.
     * @param obj the intersection
     * @param ray the ray being traced
     * @return the computed colour at the intersection point
     */
    private Color applyShading(Intersection obj, Ray ray) {
        double[] rgb = {0.0, 0.0, 0.0};

        Vector intersection = ray.origin.add(ray.direction.mul(obj.t)); //Calculate intersection point
        Vector normal = obj.hit.getNormal(intersection); //Surface normal at point of intersection
        normal.normalise();

        double diffuseSum = 0.0;
        double specularSum = 0.0;

        ArrayList<Vector> samples = renderScene.getLight().sampleLightSource(sampleCount); //sample the light source

        for (Vector sample : samples) {

            Vector toLight = sample.sub(intersection); //Direction from intersection to sample point
            toLight.normalise();

            Vector offsetOrigin = intersection.add(toLight.mul(EPSILON)); //Offset origin to avoid self-shadowing
            Ray toLightRay = new Ray(offsetOrigin, toLight);

            //Accumulate diffuse and specular values only if the light source is visible
            if(!lightSourceOccluded(toLightRay, sample)){

                //compute diffuse contribution
                double dp = calculateDP(toLight, normal, ray);
                diffuseSum += dp;

                //compute specular contribution if shininess greater than zero
                if(obj.hit.shininess > 0){
                    Vector lightToIntersection = intersection.sub(sample);
                    lightToIntersection.normalise();

                    Vector intersectionToRayOrigin = ray.origin.sub(intersection);
                    intersectionToRayOrigin.normalise();

                    double spec = calculateSpec(lightToIntersection, normal, intersectionToRayOrigin, obj.hit.shininess);
                    specularSum += spec;
                }
            }
        }

        double diff;
        double spec;
        diff = diffuseSum / sampleCount; //Average diffuse
        spec = specularSum / sampleCount; //Average specular

        rgb = applyAmbient(rgb, obj.hit, renderScene.getLight()); //Add ambient contribution

        rgb = applyDiffuse(rgb, obj.hit, renderScene.getLight(), diff); //Add diffuse contribution

        rgb = applySpecular(rgb, obj.hit, renderScene.getLight(), spec); //Add specular contribution

        rgb = clampRGB(rgb); //Clamp rgb values in range [0, 1]
        return Color.color(rgb[0], rgb[1], rgb[2]);
    }


    /**
     * Checks if the light source is visible or blocked
     * from a certain point.
     * @param ray a ray from the intersection to the light source
     * @param lightPos the light position current being checked
     * @return true if the light source is visible, false otherwise.
     */
    private boolean lightSourceOccluded(Ray ray, Vector lightPos) {

        Vector toLight = lightPos.sub(ray.origin);
        double distanceToLight = toLight.magnitude();

        return renderScene.isOccluded(ray, distanceToLight);

//        Intersection obj = renderScene.closestHit(ray);
//
//        if (obj == null) {
//            return true;
//        }
//        if ((obj.t > 0 && obj.t < distanceToLight)) { //Checks if object between ray origin and light source
//            return false;
//        } else {
//            return true;
//        }
    }




    /**
     * Applies ambient shading.
     * @param rgb the current colour at the point
     * @param obj the object being shaded
     * @param light the light source
     * @return the rgb array updated after applying ambient shading.
     */
    private double[] applyAmbient(double[] rgb, SceneObject obj, LightSource light) {
        rgb[0] += obj.ambient.getRed() * light.color.getRed();
        rgb[1] += obj.ambient.getGreen() * light.color.getGreen();
        rgb[2] += obj.ambient.getBlue() * light.color.getBlue();
        return rgb;
    }

    /**
     * Applies diffuse shading.
     * @param rgb the current colour at the point
     * @param obj the object being shaded
     * @param light the light source
     * @param diff the diffuse contribution
     * @return the rgb array updated after applying diffuse shading.
     */
    private double[] applyDiffuse(double[] rgb, SceneObject obj, LightSource light, double diff) {
        rgb[0] += obj.diffuse.getRed() * light.color.getRed() * diff;
        rgb[1] += obj.diffuse.getGreen() * light.color.getGreen() * diff;
        rgb[2] += obj.diffuse.getBlue() * light.color.getBlue() * diff;
        return rgb;
    }


    /**
     * Applies specular shading.
     * @param rgb the current colour at the point
     * @param obj the object being shaded
     * @param light the light source
     * @param spec the specular contribution.
     * @return the rgb array updated after applying specular shading.
     */
    private double[] applySpecular(double[] rgb, SceneObject obj, LightSource light, double spec) {
        rgb[0] += obj.specular.getRed() * light.color.getRed() * spec;
        rgb[1] += obj.specular.getGreen() * light.color.getGreen()  * spec;
        rgb[2] += obj.specular.getBlue() * light.color.getBlue()  * spec;
        return rgb;
    }

    /**
     * Calculates the dot product between the surface normal
     * and the vector to the light source.
     * Triangle normals may face the wrong direction and
     * are flipped here before calculating the dp.
     * @param toLight a vector to the light source
     * @param normal the surface normal at the point being shaded
     * @param ray the ray being traced
     * @return the dot product between the surface normal
     *         and the vector to the light source.
     */
    private double calculateDP(Vector toLight, Vector normal, Ray ray){
        return Math.max(0, toLight.dot(normal));
    }


    /**
     * Calculates the specular component
     * @param lightToIntersection a vector from the light to the intersection point.
     * @param normal the surface normal at the point being shaded
     * @param intersectionToRayOrigin a vector from the intersection point to the ray origin.
     * @param shininess the shininess coefficient of the object being shaded
     * @return the specular component
     */
    private double calculateSpec(Vector lightToIntersection, Vector normal, Vector intersectionToRayOrigin, double shininess) {
        Vector secondaryRay = lightToIntersection.sub(normal.mul(2 * lightToIntersection.dot(normal)));
        secondaryRay.normalise();

        return Math.pow((Math.max(0, secondaryRay.dot(intersectionToRayOrigin))), shininess);
    }


    /**
     * Clamps values in the range [0, 1].
     * @param rgb the rgb array to be clamped.
     * @return the rgb array after clamping has taken place
     */
    private double[] clampRGB(double[] rgb){
        if(rgb[0] > 1) rgb[0] = 1;
        if(rgb[1] > 1) rgb[1] = 1;
        if(rgb[2] > 1) rgb[2] = 1;

        if(rgb[0] < 0) rgb[0] = 0;
        if(rgb[1] < 0) rgb[1] = 0;
        if(rgb[2] < 0) rgb[2] = 0;

        return rgb;
    }


    public void setShadowQualityLow(){
        sampleCount = 1;
    }

    public void setShadowQualityHigh(){
        sampleCount = 16;
    }

    public void setShadowQualityVeryHigh(){
        sampleCount = 64;
    }

    public void setWritableImage(WritableImage image){
        this.image = image;
        pixelWriter = image.getPixelWriter();
    }


}
