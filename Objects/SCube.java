package Objects;

public class SCube extends SObject {

    public SCube() {
        super();
        genData();
    }


    @Override
    protected void genData() {

        // Expanded the coordinates used for the Lab 5 to make all 6 faces

        numVertices = 36;
        numIndices = 36;

        vertices = new float[]{
                //front
                -1, -1,  1,
                1, -1,  1,
                1,  1,  1,
                -1,  1,  1,
                //back
                -1, -1, -1,
                -1,  1, -1,
                1,  1, -1,
                1, -1, -1,
                //back
                -1,  1, -1,
                -1,  1,  1,
                1,  1,  1,
                1,  1, -1,
                //bottom
                -1, -1, -1,
                1, -1, -1,
                1, -1,  1,
                -1, -1,  1,
                //right
                1, -1, -1,
                1,  1, -1,
                1,  1,  1,
                1, -1,  1,
                //left
                -1, -1, -1,
                -1, -1,  1,
                -1,  1,  1,
                -1,  1, -1
        };

        normals = new float[]{
                //front
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                0, 0, 1,
                //back
                0, 0,-1,
                0, 0,-1,
                0, 0,-1,
                0, 0,-1,
                //top
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                //bottom
                0,-1, 0,
                0,-1, 0,
                0,-1, 0,
                0,-1, 0,
                //right
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                1, 0, 0,
                //left
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0,
                -1, 0, 0
        };

        textures = new float[]{
                //fornt
                0, 0,
                1, 0,
                1, 1,
                0, 1,
                //back
                1, 0,
                1, 1,
                0, 1,
                0, 0,
                //top
                0, 1,
                0, 0,
                1, 0,
                1, 1,
                //bottom
                1, 1,
                0, 1,
                0, 0,
                1, 0,
                //left
                1, 0,
                1, 1,
                0, 1,
                0, 0,
                //right
                0, 0,
                1, 0,
                1, 1,
                0, 1
        };

        indices = new int[]{
                //front
                0, 1, 2, 0, 2, 3,
                //back
                4, 5, 6, 4, 6, 7,
                //top
                8, 9, 10, 8, 10, 11,
                //bottom
                12, 13, 14, 12, 14, 15,
                //right
                16, 17, 18, 16, 18, 19,
                //left
                20, 21, 22, 20, 22, 23
        };
    }

}