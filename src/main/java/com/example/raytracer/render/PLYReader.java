package com.example.raytracer.render;

import com.example.raytracer.geometry.SceneObject;
import com.example.raytracer.helper.Vector;
import com.example.raytracer.geometry.Triangle;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;


/**
 * This class is responsible for reading in the contents of a PLY file
 * and constructing triangle objects from the data.
 */
public class PLYReader {

    public PLYReader(){}

    /**
     * Reads the contents of a PLY file and returns a list of primitives
     * extracted from the file.
     * @param file the file to read
     * @param scale the scale to be applied when transforming the vertex data
     * @param offset the offset to be applied when transforming the vertex data
     * @return a list of all the primitives extracted from the PLY file
     */
    public ArrayList<SceneObject> readPLYFile(File file,
                                              double scale,
                                              Vector offset
                            ){

        ArrayList<SceneObject> triangles = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(file))){

            String line;
            int numVertices = 0;
            int numFaces = 0;

            boolean inHeader = true;

            while(inHeader){
                line = br.readLine();
                line = line.trim();

                if(line.startsWith("element vertex")){
                    String[] elements = line.split(" ");
                    numVertices = Integer.parseInt(elements[2]);
                }
                else if(line.startsWith("element face")){
                    String[] elements = line.split(" ");
                    numFaces = Integer.parseInt(elements[2]);
                } else if(line.startsWith("end_header")){
                    inHeader = false;
                }
            }

            Vector[] vertices = new Vector[numVertices];

            for(int i = 0; i < numVertices; i++){
                line = br.readLine();

                String[] elements = line.split(" ");

                double x = Double.parseDouble(elements[0]);
                double y = Double.parseDouble(elements[1]);
                double z = Double.parseDouble(elements[2]);

                vertices[i] = transformVector(new Vector(x,y,z), scale, offset);
            }

            for(int j = 0; j < numFaces; j++){
                line = br.readLine();

                String[] elements = line.split(" ");

                int indexX = Integer.parseInt(elements[1]);
                int indexY = Integer.parseInt(elements[2]);
                int indexZ = Integer.parseInt(elements[3]);

                Triangle triangle = new Triangle(vertices[indexX],vertices[indexY],vertices[indexZ]);
                triangles.add(triangle);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return  triangles;
    }

    Vector transformVector(Vector vector, double scale, Vector offset){
        return new Vector(
                (vector.x * scale) + offset.x,
                (vector.y * scale) + offset.y,
                (-vector.z * scale) + offset.z
        );
    }
}
