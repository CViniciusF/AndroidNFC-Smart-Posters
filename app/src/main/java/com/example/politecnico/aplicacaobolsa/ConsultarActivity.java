package com.example.politecnico.aplicacaobolsa;

import android.content.Intent;
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
import android.widget.EditText;
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

import modelo.Usuario;
import modelo.UsuarioService;

public class ConsultarActivity extends AppCompatActivity {
    //public static  String URL = "http://200.132.36.197:8080/LoginJSONWeb/web/WEB-INF/classes/servlets/ConsultarServlet";
    //public static  String URL = "http://200.132.36.197:8080/LoginJSONWeb/ConsultarServlet";
    public static  String URL = "http://192.168.90.66:8080/LoginJSONWeb/ConsultarServlet";
    List<Usuario> Lista = new ArrayList<Usuario>();

    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar);
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[]{URL});

    }
            //Botão de nova consulta enviando o id de usuario e o id do artigo para a nova activity.
        public void novaConsulta(View view) {
            Bundle bundle = getIntent().getExtras();
            String idu = bundle.getString("idusuario");
            Intent it = new Intent(this, Main2Activity.class);
            it.putExtra("id", id);
            it.putExtra("idusuario", idu);
            startActivity(it);

        }

        public void lertxt(View view){
            Bundle bundle = getIntent().getExtras();
            String idu = bundle.getString("idusuario");
            System.out.println("Id do usuario na ConsultarActivity: " + idu);
            Intent it = new Intent(getBaseContext(), TextoActivity.class);
            it.putExtra("id", id);
            it.putExtra("idusuario", idu);
            startActivity(it);

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
            JSONObject objTudo=new JSONObject();

            Bundle bundle = getIntent().getExtras();
            id = (String) bundle.get("id");
            Log.d("DENTRODAMENSAGEM", id);

            JSONObject obj =new JSONObject();
            Bundle bundle2 = getIntent().getExtras();
            String idu = bundle2.getString("idusuario");
            String testeRetorno = bundle2.getString("retorno");
            System.out.println("Teste retorno = " + testeRetorno);
            obj.put("id",id);
            obj.put("idusuario", idu);
            //OBJETO PARA INDICAR QUE É A PRIMEIRA VEZ QUE A ETIQUETA TA SENDO LIDA NESSA "SESSÃO";
            obj.put("primeiracons", "1");
            //OBJETO PARA INDICAR QUE O USUARIO APERTOU O BOTAO DE RETORNO NA TextoActivity então nao deve adicionar mais uma interação;
            obj.put("testeretorno", testeRetorno);
            array.put(obj);
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

            TextView nome = (TextView)findViewById(R.id.nome);
            //TextView email = (TextView)findViewById(R.id.email);
            TextView senha = (TextView)findViewById(R.id.senha);

            JSONObject jsonRootObject = new JSONObject(strJson);
            JSONArray jsonArray = jsonRootObject.optJSONArray("array");

            JSONArray jsonArray2 = jsonRootObject.optJSONArray("array2");

            TextView rec1 = (TextView)findViewById(R.id.rec1);
            TextView rec2 = (TextView)findViewById(R.id.rec2);
            TextView rec3 = (TextView)findViewById(R.id.rec3);
            String jsonObjectCons = jsonRootObject.optString("count");
            System.out.println("jsonObjectCons = " + jsonObjectCons);
            Toast.makeText(ConsultarActivity.this,"Você consultou esse poster " + jsonObjectCons + " vezes",Toast.LENGTH_SHORT).show();

            for(int i=0; i<jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String nomet = jsonObject.optString("titulo").toString();
                String emailt = jsonObject.optString("textoart").toString();
                String senhat = jsonObject.optString("nomeautor").toString();
                nome.setText(nomet);
                //email.setText(emailt);
                senha.setText(senhat);

                JSONObject jsonObjectRec2 = jsonArray2.getJSONObject(0);
                String rec1s = jsonObjectRec2.optString("titulo").toString();
                JSONObject jsonObjectRec3 = jsonArray2.getJSONObject(1);
                String rec2s = jsonObjectRec3.optString("titulo").toString();
                JSONObject jsonObjectRec4 = jsonArray2.getJSONObject(2);
                String rec3s = jsonObjectRec4.optString("titulo").toString();

                rec1.setText(rec1s);
                rec2.setText(rec2s);
                rec3.setText(rec3s);

            }


        } catch (JSONException e) {e.printStackTrace();}
    }
        /*
    private void imprimeJsonRec(String strJson){
        try {

            TextView rec1 = (TextView)findViewById(R.id.rec1);
            TextView rec2 = (TextView)findViewById(R.id.rec2);
            TextView rec3 = (TextView)findViewById(R.id.rec3);

            JSONObject jsonRootObject = new JSONObject(strJson);
            JSONArray jsonArray = jsonRootObject.optJSONArray("array");

            for(int i=0; i<jsonArray.length(); i++){

                JSONObject jsonObjectRec = jsonArray.getJSONObject(i);
                String rec1s = jsonObjectRec.optString("titulo").toString();
                String rec2s = jsonObjectRec.optString("titulo").toString();
                String rec3s = jsonObjectRec.optString("titulo").toString();
                rec1.setText(rec1s);
                rec2.setText(rec2s);
                rec3.setText(rec3s);
                //email.setText(emailt);


            }


        } catch (JSONException e) {e.printStackTrace();}
    } */

}
