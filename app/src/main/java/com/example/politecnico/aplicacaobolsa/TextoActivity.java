package com.example.politecnico.aplicacaobolsa;

import android.content.Intent;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import android.view.View.OnClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;


import modelo.Usuario;
import modelo.UsuarioService;

public class TextoActivity extends AppCompatActivity {

    //public static  String URL = "http://200.132.36.197:8080/LoginJSONWeb/ConsultarServlet";
    public static  String URL = "http://192.168.90.66:8080/LoginJSONWeb/ConsultarServlet";

    List<Usuario> Lista = new ArrayList<Usuario>();
    private RatingBar ratingBar;
    private TextView txtValorAvaliacao;
    private Button btnSubmit;
    private String id;
    private float notaenviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[]{URL});
    }



    public void retornar(View view) {
        Intent it = new Intent(this, ConsultarActivity.class);
        Bundle bundle = getIntent().getExtras();
        String idu = bundle.getString("idusuario");
        it.putExtra("id", id);
        it.putExtra("idusuario", idu);
        it.putExtra("retorno", "retornou");
        startActivity(it);

    }


    public void enviarnota(View view){
        Toast.makeText(TextoActivity.this, "Você deu nota " + String.valueOf(ratingBar.getRating() + " para este Artigo."), Toast.LENGTH_SHORT).show();
        notaenviar =(ratingBar.getRating());
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[]{URL});
    }



    private class GetXMLTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }




        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }

        private InputStream getHttpConnection(String urlString)
                throws Exception {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            JSONArray array = new JSONArray();
            JSONArray arraynt = new JSONArray();
            JSONObject objTudo=new JSONObject();

            Bundle bundle = getIntent().getExtras();

            id = (String) bundle.get("id");
            Log.d("DENTRODAMENSAGEM", id);

            JSONObject obj =new JSONObject();
            obj.put("id",id);
            array.put(obj);
            if(notaenviar != 0.0) {
                String idu = bundle.getString("idusuario");
                System.out.println("Id do usuario quando aperta em enviar nota: " + idu);
                //o id do artigo ja esta sendo enviado anteriormente.
                obj.put("idusuario", idu);
                array.put(obj);
                obj.put("ntandroid", notaenviar);
                array.put(obj);
                System.out.println("valor do notaenviar:" + notaenviar);
            }
            objTudo.put("consultar", array);

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();
                httpConnection.getOutputStream().write(objTudo.toString().getBytes());

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String output) {
            imprimeJson(output);
            Log.d("*****************", "result= " + output);
        }
    }

    private void imprimeJson(String strJson){
        try {

            TextView texto = (TextView)findViewById(R.id.texto);
            TextView txtValorAvaliacao = (TextView)findViewById(R.id.txtValorAvaliacao);
            JSONObject jsonRootObject = new JSONObject(strJson);
            JSONArray jsonArray = jsonRootObject.optJSONArray("array");
            String txtnt = jsonRootObject.optString("media").toString();
            txtValorAvaliacao.setText(txtnt);
            System.out.println("Media de notas: " + txtnt);


            for(int i=0; i<jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String textoa = jsonObject.optString("textoart").toString();
                texto.setText(textoa);


            }


        } catch (JSONException e) {e.printStackTrace();}
    }

}
