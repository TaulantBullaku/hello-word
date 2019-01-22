Checklist per codereview, "item" utilizzati


- Specifiche esplicitate in maniera chiara ed esaustiva
- Vengono rispettate le specifiche
- Il nome della classe è chiaro rispetto alla funzionalità
- Hardcoding sul codice
- Adeguati commenti per spiegare le parti critiche
- Correttezza del nome delle variabili, nomi chiari chi facilitino a capire il loro utilizzo
- Che le eccezzioni siano gestite adeguatamente
- Eliminazione dei riferimenti a oggetti obsoleti
- Ridurre al minimo l'accessibilità di classi e membri
- Preferenza delle interfacce alle classi astratte
- Utilizzo delle interfacce solo per definire i tipi
- Favorire metodi generici




// Nella prima parte vi è il metodo takePhoto(#167) da tenere in considerazione, allego relativo link per visualizzare l'intero Branch.
// https://github.com/PrandiniUniPD/OCRCameraDemo/tree/takePhoto/app/src/main/java/group2/software/ingegneria/elementi/ocrcamerademo


package group2.software.ingegneria.elementi.ocrcamerademo;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.nio.ByteBuffer;

public class Homepage extends AppCompatActivity
{

    /**
     * |||||||||||||||||||||||
     * ||   UI Components   ||
     * |||||||||||||||||||||||
     */
    private Button btnTakePhoto;
    private TextureView cameraPreview;

    /**
     * |||||||||||||||||||||||||||
     * ||   Variabili private   ||
     * |||||||||||||||||||||||||||
     */
    private CameraDevice cameraDevice;
    private Handler cameraHandler;

    //The code that identifies the camera request
    private final int CAMERA_REQUEST_CODE = 277;

    //The interface to monitor camera states
    //Implemented by @Leonardo Rossi
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera)
        {
            //createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Check if camera permissions have already been requested
        //Implemented by @Leonardo Rossi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            String[] permissions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissions, CAMERA_REQUEST_CODE);
        }

        //Initialization of UI components
        //Implemented by @Leonardo Rossi
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        cameraPreview = findViewById(R.id.cameraPreview);

        //Definition of listeners
        //Implemented by @Leonardo Rossi
        cameraPreview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
            {
                //openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        //Implemented by @Leonardo Rossi
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                takePhoto();
            }
        });
    }

    /**
     * |||||||||||||||||||||||
     * ||   Metodi privati  ||
     * |||||||||||||||||||||||
     */


    private void openCamera()
    {
        //TODO: Implement the method to open the camera
    }

    private void createCameraPreview()
    {
        //TODO: Implement the method to create camera preview
    }

    /**
     * Saves the Photo Bitmap, previously converted into Base64 String, into the current activity sharedPreferences file
     * @modify cameraDevice
     * @implementation Alberto Valente, Taulant Bullaku
     */

    private void takePhoto()
    {
        if(cameraDevice == null)
        {
            Log.e("cameraDevice", "cameraDevice is null");
            return;
        }

        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try
        {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null)
            {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if(jpegSizes != null && jpegSizes.length > 0)
            {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener()
            {
                @Override
                public void onImageAvailable(ImageReader reader)
                {
                    //acquires the last image and delivers it to a buffer
                    Image image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] photoByteArray = new byte[buffer.capacity()];
                    buffer.get(photoByteArray);

                    //to consider only if the Bitmap of the photo is required
                    Bitmap photoBitmap = BitmapFactory.decodeByteArray(photoByteArray,0, photoByteArray.length);
                    //

                    //Converts the byte array related to the given photo to a String in Base64 format
                    String photoBitmapToString = Base64.encodeToString(photoByteArray, Base64.DEFAULT);

                    //Print photoBitmapToString for testing purpose
                    Log.e("Bitmap----------->>>",photoBitmapToString);

                    //create sharedPref file to save the Bitmap of last taken photo in a String form
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("photoBitmap", photoBitmapToString);
                }
            };
            reader.setOnImageAvailableListener(readerListener, cameraHandler);
        }
        catch(CameraAccessException e)
        {
            e.printStackTrace();
        }

    }
}



// di seguito riporto onLoginSuccess, onLoginFailure come proposte per revisione.
//allego link con Branch completo per eventuali necessità.
//https://github.com/PrandiniUniPD/OCRCamera/tree/discussionForum/forum/src/main/java/unipd/se18/ocrcamera/forum




package unipd.se18.ocrcamera.forum;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import unipd.se18.ocrcamera.forum.viewmodels.Login_VM;


/**
 * A fragment where a user can login to the forum service
 *
 * @author Leonardo Rossi (g2), Alberto Valente (g2), Taulant Bullaku (g2)
 */
public class ForumLogin extends Fragment {

    /**
     * ***************************
     * **   GLOBAL VARIABLES    **
     * ***************************
     */

    /**
     * String used for logs to identify the fragment throwing it
     */
    private final String LOG_TAG = String.valueOf(R.string.logTagForumLogin);

    /**
     * String used to indicate that no username has been passed
     */
    private final String LOG_NULL_USERNAME = String.valueOf(R.string.logNullUsername);

    /**
     * String used to indicate that no error message has been passed
     */
    private final String LOG_NULL_ERROR_MESSAGE = String.valueOf(R.string.logNullErrorMessage);

    /**
     * Key to identify the username passed to ShowPosts instance
     */
    private final String KEY_USERNAME = String.valueOf(R.string.keyUsername);

    //UI objects declaration
    private EditText usernameEditText;
    private EditText pwdEditText;
    private Button loginButton;
    private TextView registerEditText;

    //Credentials strings declaration
    private String userName;
    private String userPwd;

    //Corresponding view model declaration
    private Login_VM viewModel;

    public ForumLogin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //View model initialization
        viewModel = ViewModelProviders.of(this).get(Login_VM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //UI object initialization
        usernameEditText = view.findViewById(R.id.usernameEditText);
        pwdEditText = view.findViewById(R.id.pwdEditText);
        loginButton = view.findViewById(R.id.loginButton);
        registerEditText = view.findViewById(R.id.registerEditText);

        /*
        If the view is launched after a successful registration process, the username field is
        already set to the registration one to speed up the login process and to provide feedback
         */
        if (savedInstanceState != null) {
            usernameEditText.setText(savedInstanceState.getString(KEY_USERNAME));
        }

        //Definition of view model listener
        viewModel.setForumLoginListener(new Login_VM.ForumLoginListener() {

            /**
             * The method of the listener is triggered when the database response
             * to the login request is positive and the view model requests to load
             * the ShowPost fragment, so that the user can access the forum contents
             *
             * @param username The username of the user that successfully logged in
             * @author Alberto Valente (g2), Taulant Bullaku (g2)
             */
            @Override
            public void onLoginSuccess(String username) {

                if(username != null) {
                    //creates the username bundle
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USERNAME, username);

                    //creates an instance of the fragment to be launched
                    ShowPosts showPostsFragment = new ShowPosts();
                    //passes the bundle to the fragment as an argument
                    showPostsFragment.setArguments(bundle);

                    //performs the fragment transition
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.fragmentContainer,
                                    showPostsFragment
                            )
                            .commit();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_USERNAME);
                }
            }

            /**
             * The method of the listener is triggered when the database response to
             * the login request is negative and the view model requests to show the
             * user an error message, that is performed through the use of a toast
             *
             * @param message The error message about what was wrong with the login request
             * @author Alberto Valente (g2), Taulant Bullaku (g2)
             */
            @Override
            public void onLoginFailure(String message) {

                if (message != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d(LOG_TAG, LOG_NULL_ERROR_MESSAGE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            /**
             * When the login button is clicked a login request is sent through the view model
             * so that the user can access the forum providing his credentials
             *
             * @param v the view where the click event is performed
             */
            @Override
            public void onClick(View v) {

                /*
                When the login button is clicked, the username and password are gathered from
                the EditText objects which have been filled by the user
                 */
                userName = usernameEditText.getText().toString();
                userPwd = pwdEditText.getText().toString();

                //Checks whether the user left a blank field
                if (!userName.equals("") && !userPwd.equals("")) {

                    //If not, the credentials are handed to the view model method to be checked
                    viewModel.loginToForum(requireContext(), userName, userPwd);
                }
                else {

                    //If so, a warning toast is shown to the user
                    Toast.makeText(getContext(), R.string.loginButtonToast, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        registerEditText.setOnClickListener(new View.OnClickListener(){

            /**
             * When the register label is clicked a new ForumRegister fragment is launched
             * in order to make the user create the required account
             *
             * @param v the view where the click event is performed
             */
            @Override
            public void onClick(View v) {

                //creates an instance of the fragment to be launched
                ForumRegister forumRegisterFragment = new ForumRegister();

                //performs the fragment transaction
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragmentContainer,
                                forumRegisterFragment
                        )
                        .commit();
            }
        });

    }

}

