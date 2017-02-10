package com.example.politecnico.aplicacaobolsa;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import modelo.Usuario;
import modelo.UsuarioService;


public class CadastrarUsuarioActivity extends AppCompatActivity {

    //public static  String URL = "http://200.132.36.197:8080/LoginJSONWeb/web/WEB-INF/classes/servlets/CadastraUsuario";
    //public static  String URL = "http://200.132.36.197:8080/LoginJSONWeb/CadastraUsuario";
    public static  String URL = "http://192.168.90.66:8080/LoginJSONWeb/CadastraUsuario";

    private EditText nome;
    private EditText login;
    private EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cadastrar_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CadastrarU(View view) {
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[]{URL});
        //if (nome.getText().toString() != null && login.getText().toString() != null && senha.getText().toString() != null) {
          //Toast.makeText(getApplicationContext(), "Dados Invalidos", Toast.LENGTH_LONG).show();

    }

    public void cancelar(View view) {
     Intent it = new Intent(this, LoginActivity.class);
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



            EditText  login = (EditText) findViewById(R.id.login);
            EditText senha = (EditText) findViewById(R.id.senha);
            EditText nome = (EditText) findViewById(R.id.nome);


            JSONArray array = new JSONArray();
            JSONObject objTudo=new JSONObject();

            JSONObject obj =new JSONObject();
            obj.put("usuario",login.getText().toString());
            obj.put("senha",senha.getText().toString());
            obj.put("nome",nome.getText().toString());
            array.put(obj);
            objTudo.put("cadastrar", array);


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
            JSONObject jsonRootObject = new JSONObject(strJson);

            JSONArray jsonArray = jsonRootObject.optJSONArray("usuarioLogado");

            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String retorno = jsonObject.optString("retorno").toString();

                if(retorno.equals("true")){
                    Intent it = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(it);
                }else{
                    Toast.makeText(getBaseContext(), "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show();
                }

                Log.d("*****************", "resultFiltrado= " + retorno);

            }

        } catch (JSONException e) {e.printStackTrace();}
    }

}
