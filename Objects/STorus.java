package Objects;

public class STorus extends SObject {

    private float rMaj;
    private float rMin;
    private int segMaj;
    private int segMin;

    public STorus (float rMaj, float rMin, int segMaj, int segMin) {
        super();
        this.rMaj = rMaj;
        this.rMin = rMin;
        this.segMaj = segMaj;
        this.segMin = segMin;
    }

    @Override
    protected void genData() {
        /*
            Torus geometry built from equations as defined: https://electronut.in/torus/
        */
        // calculate vertices and indicies
        int totalVertices = segMaj * segMin;
        int totalIndices = segMaj * segMin * 6;

        //Allocate arrays for vertex attributes and indices
        vertices = new float[totalVertices * 3];
        normals = new float[totalVertices * 3];
        textures = new float[totalVertices * 2];
        indices = new int[totalIndices];

        //initialise counters
        int vertexIndex = 0;
        int index = 0;

        // initialise variabes for x,y,z coordinates and the textures
        float aMaj, aMin;
        float x, y, z, u, v;

        //loop through the major circles
        for (int i = 0; i < segMaj; i++) {
            // and the minor ones
            for (int j = 0; j < segMin; j++) {

                // calculate angle
                aMaj = (float)(2.0 * PI * i / segMaj);
                aMin = (float)(2.0 * PI * j / segMin);

                //calc x,y,z
                x = (rMaj + rMin * cos(aMaj)) * cos(aMin);
                y = (rMaj + rMin * cos(aMaj)) * sin(aMin);
                z = rMin * sin(aMaj);

                // horizontal and veritcal textures
                u = (float) (1.0 * i / segMaj);
                v = (float) (1.0 * j / segMin);

                // normal
                normals[vertexIndex] = x - rMaj * cos(aMin);
                normals[vertexIndex + 1] = y - rMaj * sin(aMin);
                normals[vertexIndex + 2] = z;

                // textures
                textures[vertexIndex / 3 * 2] = u;
                textures[vertexIndex / 3 * 2 + 1] = v;

                // textures
                vertices[vertexIndex] = x;
                vertices[vertexIndex+1] = y;
                vertices[vertexIndex+2] = z;

                int currV = i * segMin + j;
                int nextRowV = (i + 1) % segMaj * segMin + j;
                int nextColV = i * segMin + (j + 1) % segMin;
                int nextRowColV = (i + 1) % segMaj * segMin + (j + 1) % segMin;

                // assign the values of indices while increment location in array#
                // triangle 1
                indices[index++] = currV;
                indices[index++] = nextRowV;
                indices[index++] = nextColV;

                // triangle 2
                indices[index++] = nextColV;
                indices[index++] = nextRowV;
                indices[index++] = nextRowColV;

                // increment index
                vertexIndex += 3;
            }
        }

        // Set total counts
        numVertices = totalVertices;
        numIndices = totalIndices;
    }
}