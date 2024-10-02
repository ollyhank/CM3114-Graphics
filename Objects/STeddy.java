package Objects;

import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;

import Basic.Vec3;

public class STeddy extends SObject {

    int numFaces;

    public STeddy() {
        genData();
    }

    @Override
    protected void genData() {
        try {
            String filePath = "Teddy.ply";
            File f = new File(filePath);
            Scanner scanner = new Scanner(f);

            boolean readingVertices = true;

            String line;
            while ((line = scanner.nextLine()) != null) {
                if (line.startsWith("element vertex")) {
                    // Extract the number of vertices
                    numVertices = Integer.parseInt(line.split(" ")[2]);
                } else if (line.startsWith("element face")) {
                    // Extract the number of faces
                    numIndices = Integer.parseInt(line.split(" ")[2]);
                    readingVertices = false;  // Switch to reading faces
                } else if (!readingVertices && line.startsWith("end_header")) {
                    // End of header, break the loop
                    break;
                }
            }

            // Read vertex data
            vertices = new float[numVertices*3];
            for(int i = 0; i < numVertices; i++){
                float x = scanner.nextFloat();
                float y = scanner.nextFloat();
                float z = scanner.nextFloat();
                vertices[i*3] = x;
                vertices[i*3+1] = y;
                vertices[i*3+2] = z;
            }

            // Read incides data
            indices = new int[numIndices * 3];
            for(int i = 0; i < numIndices; i++){
                scanner.next(); // Skip over the '3'
                indices[i*3] = scanner.nextInt();
                indices[i*3+1] = scanner.nextInt();
                indices[i*3+2] = scanner.nextInt();
            }

            // Read index data
            normals = new float[numVertices * 3];
            //ec3 v1 = new Vec3();
            //Vec3 v2 = new Vec3();
            //Vec3 v3 = new Vec3();
            System.out.println("\n" + numIndices);
            System.out.println("\n" + numVertices);

            Vec3[] normalsVector = new Vec3[numVertices];

            for (int i = 0; i < numVertices; i++) {
                normalsVector[i] = new Vec3();
            }

            for(int i = 0; i < numVertices; i += 3) {

                int i1 = indices[i];
                int i2 = indices[i+1];
                int i3 = indices[i+2];

                Vec3 v1 = new Vec3(vertices[i1 * 3], vertices[i1 * 3 + 1], vertices[i1 * 3 + 2]);
                Vec3 v2 = new Vec3(vertices[i2 * 3], vertices[i2 * 3 + 1], vertices[i2 * 3 + 2]);
                Vec3 v3 = new Vec3(vertices[i3 * 3], vertices[i3 * 3 + 1], vertices[i3 * 3 + 2]);

                Vec3 u = v2.minus(v1);
                Vec3 v = v3.minus(v1);

                Vec3 n = u.cross(v);
                n.normalize();

                if (Float.isNaN(n.getX()) || Float.isNaN(n.getY()) || Float.isNaN(n.getZ())) {
                    System.out.println("It's NaN!");
                    normals[i * 3] = 0;
                    normals[i * 3 + 1] = 1;
                    normals[i * 3 + 2] = 0;
                } else {
                    normals[i1*3] += n.getX();
                    normals[i1*3+1] += n.getY();
                    normals[i1*3+2] += n.getZ();

                    normals[i2*3] += n.getX();
                    normals[i2*3+1] += n.getY();
                    normals[i2*3+2] += n.getZ();

                    normals[i3*3] += n.getX();
                    normals[i3*3+1] += n.getY();
                    normals[i3*3+2] += n.getZ();
                }
            }
Z


                /*
                //System.out.println("Indices: " + i1 + ", " + i2 + ", " + i3);


                //System.out.println("Vertex " + i1 + ": (" + vertices[i1 * 3] + ", " + vertices[(i1 * 3) + 1] + ", " + vertices[(i1 * 3) + 2] + ")");
                //System.out.println("Vertex " + i2 + ": (" + vertices[i2 * 3] + ", " + vertices[(i2 * 3) + 1] + ", " + vertices[(i2 * 3) + 2] + ")");
                //System.out.println("Vertex " + i3 + ": (" + vertices[i3 * 3] + ",         " + vertices[(i3 * 3 )+ 1] + ", " + vertices[(i3 * 3) + 2] + ")");


                v1.setVector(vertices[i1*3], vertices[i1*3+1], vertices[i1*3+2]);
                v2.setVector(vertices[i2*3], vertices[i2*3+1], vertices[i2*3+2]);
                v3.setVector(vertices[i3*3], vertices[i3*3+1], vertices[i3*3+2]);

                Vec3 u = v2.minus(v1);
                Vec3 v = v3.minus(v1);

                //System.out.println("Edge1: " + edge1.getX() + ", " + edge1.getY() + ", " + edge1.getZ() + ", ");
                //System.out.println("Edge2: " + edge2.getX() + ", " + edge2.getY() + ", " + edge2.getZ() + ", ");

                Vec3 faceNormal = u.cross(v);

                faceNormal.normalize();

                //                if ((faceNormal.getX() == 0) || (faceNormal.getY() == 0) || (faceNormal.getZ() == 0) ) {
                if (Math.abs(faceNormal.len() - 0.0f) > 1e-6) {
                    faceNormal.normalize();
                    //System.out.println("Triangle: (" + Arrays.toString(v1.getVector()) + "), (" + Arrays.toString(v2.getVector()) + "), (" + Arrays.toString(v3.getVector()) + ")");
                    //System.out.println("Face Normal: " + Arrays.toString(faceNormal.getVector()));


                    normals[i1*3] += faceNormal.getX();
                    normals[i1*3+1] += faceNormal.getY();
                    normals[i1*3+2] += faceNormal.getZ();

                    normals[i2*3] += faceNormal.getX();
                    normals[i2*3+1] += faceNormal.getY();
                    normals[i2*3+2] += faceNormal.getZ();

                    normals[i3*3] += faceNormal.getX();
                    normals[i3*3+1] += faceNormal.getY();
                    normals[i3*3+2] += faceNormal.getZ();
                } else {
                    System.out.println("SOMETHINNG HAS GONE WRONG\n\n\n");
                }


            }





            // Normalize all vertex normals
            for(int i = 0; i < numVertices; ++i) {


                Vec3 v1 = new Vec3();
                v1.setVector(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]);
                v1.normalize();
                if (Float.isNaN(v1.getX()) || Float.isNaN(v1.getY()) || Float.isNaN(v1.getZ())) {
                    System.out.println("It's NaN!");
                    normals[i * 3] = 0;
                    normals[i * 3 + 1] = 1;
                    normals[i * 3 + 2] = 0;
                } else {
                    normals[i * 3] = v1.getX();
                    normals[i * 3 + 1] = v1.getY();
                    normals[i * 3 + 2] = v1.getZ();
                }

            }
            System.out.println("Number of Vertices: " + numVertices);
            for (int i = 0; i < numIndices; i++) {
                if (indices[i] < 0 || indices[i] >= numVertices) {
                    System.out.println("Invalid Vertex Index at position " + i + ": " + indices[i]);
                }
            }
            for (int i = 0; i < numVertices; ++i) {
                System.out.println("Accumulated Normal " + i + ": (" + normals[i * 3] + ", " + normals[i * 3 + 1] + ", " + normals[i * 3 + 2] + ")");
            }
            */

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}