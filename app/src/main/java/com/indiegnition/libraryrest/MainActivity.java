package com.indiegnition.libraryrest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.indiegnition.libraryrest.models.UserDTO;
import com.indiegnition.libraryrest.services.UserService;
import com.indiegnition.libraryrest.services.rest.RESTCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doUserExample();
    }
    private void doUserExample(){

        /*showLoading();*/

        final AppCompatActivity activity = this;

        UserService.getUser(1, new RESTCallback() {
            @Override
            public void onResult(Object object, int statusCode, Exception error) {

                /*hideLoading();*/

                UserDTO user = (UserDTO) object;

                switch (statusCode)
                {
                    case 200:

                        UserService.setCurrentLogedUser(user);

                        break;

                    default:
                        // Something went wrong
                }

            }
        });
    }
}
