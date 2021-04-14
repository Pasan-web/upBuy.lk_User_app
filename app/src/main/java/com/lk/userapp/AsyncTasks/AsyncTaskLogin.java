package com.lk.userapp.AsyncTasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.lk.userapp.Model.Register;
import com.lk.userapp.VerifyEmail;
import com.lk.userapp.utils.AppConfig;
import com.lk.userapp.utils.HttpPostClient;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class AsyncTaskLogin extends AsyncTask<String,String,String> {

    private final View view;

    public AsyncTaskLogin(View view) {
        this.view = view;

    }

    @Override
    protected String doInBackground(String... strings) {

       JsonObject jo = new JsonObject();
        jo.addProperty("firstname",strings[0]);
        jo.addProperty("lastname",strings[1]);
        jo.addProperty("email",strings[2]);
        jo.addProperty("password",strings[3]);
        jo.addProperty("mobile",strings[4]);
        jo.addProperty("key",strings[5]);

       String jsonText = jo.toString();

        List<BasicNameValuePair> lst = new ArrayList<>();
        lst.add(new BasicNameValuePair("parameter",jsonText));

        String response = new HttpPostClient().sendHttpPostRequest(AppConfig.loginHostUrl,lst);
        return response;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

       Register user = new Gson().fromJson(s,Register.class);
       Log.d("keyyy",user.getKey());
        try {

            if(!user.getKey().equals("error")){
                //intent
                Intent intent = new Intent(view.getContext(), VerifyEmail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("firstname",user.getFirstname());
                intent.putExtra("lastname",user.getLastname());
                intent.putExtra("email",user.getEmail());
                intent.putExtra("pw",user.getPassword());
                intent.putExtra("mobile",user.getMobile());
                intent.putExtra("verifykey",user.getKey());
                view.getContext().startActivity(intent);
            }else{
                Toast.makeText(view.getContext(),"Network Error !",Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Toast.makeText(view.getContext(),"Network Error !",Toast.LENGTH_LONG).show();

        }
    }
}
