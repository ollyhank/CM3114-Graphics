import static com.jogamp.opengl.GL3.*;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import java.util.Arrays;

import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.STeddy;

import Objects.SObject;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;


public class CGCW04{

    final GLWindow window; //Define a window
    final FPSAnimator animator=new FPSAnimator(60, true);
    final Renderer renderer = new Renderer();

    public CGCW04() {
        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities caps = new GLCapabilities(glp);
        window = GLWindow.create(caps);

        window.addGLEventListener(renderer); //Set the window to listen GLEvents
        animator.add(window);

        window.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
        //The above line will only close the window but not exit the program
        //Add System.exit(0) in dispose() function will cause it exit

        window.setTitle("Coursework 4");
        window.setSize(500,500);
        window.setVisible(true);
        animator.start();
    }

    public static void main(String[] args) {
        new CGCW04();

    }

    class Renderer implements GLEventListener {

        private Transform T = new Transform();


        //VAOs and VBOs parameters
        private int idPoint=0, numVAOs = 1;
        private int idBuffer=0, numVBOs = 1;
        private int idElement=0, numEBOs = 1;
        private int[] VAOs = new int[numVAOs];
        private int[] VBOs = new int[numVBOs];
        private int[] EBOs = new int[numEBOs];

        //Model parameters
        private int[] numElements = new int[numEBOs];

        private long vertexSize;
        private int vPosition;

        //Transformation parameters
        private int ModelView;
        private int Projection;





        @Override
        public void display(GLAutoDrawable drawable) {
            GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this

            gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

            T.initialize();
            T.scale(0.3f, 0.3f, 0.3f);

            gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );

            gl.glDrawElements(GL_TRIANGLES, numElements[0], GL_UNSIGNED_INT, 0);
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            System.exit(0);
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this
            System.out.print("GL_Version: " + gl.glGetString(GL_VERSION));
            gl.glEnable(GL_CULL_FACE);

            //compile and use the shader program
            ShaderProg shaderproc = new ShaderProg(gl, "Gouraud.vert", "Gouraud.frag");
            int program = shaderproc.getProgram();
            gl.glUseProgram(program);

            //Create the teddy
            SObject teddy = new STeddy();
            float[] vertexArray = teddy.getVertices();
            float[] normals = teddy.getNormals();
            int[] vertexIndices = teddy.getIndices();
            numElements[idElement] = teddy.getNumIndices();

            System.out.println(vertexSize);
            System.out.println(Arrays.toString(vertexArray));

            System.out.println(vertexIndices.length);
            System.out.println(Arrays.toString(vertexIndices));

            System.out.println(normals.length);
            System.out.println(Arrays.toString(normals));

            vPosition = gl.glGetAttribLocation( program, "vPosition" );
            gl.glGetAttribLocation( program, "vNormal" );


            // Get connected with the ModelView, NormalTransform, and Projection matrices
            // in the vertex shader
            ModelView = gl.glGetUniformLocation(program, "ModelView");
            Projection = gl.glGetUniformLocation(program, "Projection");


            // Generate vertex array, vertex buffer, and element buffer
            gl.glGenVertexArrays(numVAOs,VAOs,0);
            gl.glBindVertexArray(VAOs[idPoint]);

            gl.glGenBuffers(numVBOs, VBOs,0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[idBuffer]);

            gl.glGenBuffers(numEBOs, EBOs,0);
            gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);

            FloatBuffer vertices = FloatBuffer.wrap(vertexArray);
            IntBuffer elements = IntBuffer.wrap(vertexIndices);

            vertexSize = vertexArray.length*(Float.SIZE/8);
            long indexSize = vertexIndices.length*(Integer.SIZE/8);

            gl.glBufferData(GL_ARRAY_BUFFER, vertexSize, vertices, GL_STATIC_DRAW); // pay attention to *Float.SIZ
            gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize, elements, GL_STATIC_DRAW);

            // Initialize the vertex position attribute in the vertex shader
            int vPosition = gl.glGetAttribLocation( program, "vPosition" );
            gl.glEnableVertexAttribArray(vPosition);
            gl.glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, 0L);


            // This is necessary. Otherwise, the color on back face may display
            gl.glDepthFunc(GL_LESS);
            gl.glEnable(GL_DEPTH_TEST);
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int w,
                            int h) {

            GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object

            gl.glViewport(x, y, w, h);

            T.initialize();

            //projection
            if(h<1){h=1;}
            if(w<1){w=1;}
            float a = (float) w/ h;   //aspect
            if (w < h) {
                T.ortho(-1, 1, -1/a, 1/a, -1, 1);
            }
            else{
                T.ortho(-1*a, 1*a, -1, 1, -1, 1);
            }

            // Convert right-hand to left-hand coordinate system
            T.reverseZ();
            gl.glUniformMatrix4fv( Projection, 1, true, T.getTransformv(), 0 );
        }
    }
}