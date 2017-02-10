package com.example.politecnico.aplicacaobolsa;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

 NfcAdapter nfcAdapter;

    Button tglReadWrite;
    EditText txtTagContent;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //tglReadWrite = (Button)findViewById(R.id.tglReadWrite);
        txtTagContent = (EditText)findViewById(R.id.txtTagContent);
        //Armazenando o id de usuario recebido da loginactivity em uma string.

    //    String id = String.valueOf(txtTagContent);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause(){
        super.onPause();

        disableForegroundDispatchSystem();
    }




    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            //Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables != null && parcelables.length > 0){
                    readTextFromMessage((NdefMessage) parcelables[0]);
                }else{
                    Toast.makeText(this, "Nenhuma Mensagem Encontrada", Toast.LENGTH_SHORT).show();
                }


        }

    }


    private void readTextFromMessage(NdefMessage ndefMessage) {
        Bundle bundle = getIntent().getExtras();
        String idu = bundle.getString("idusuario");
        System.out.println("Id do usuario na Main2: " + idu);
       NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length > 0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            txtTagContent.setText(tagContent);

            Intent it = new Intent(getBaseContext(), ConsultarActivity.class);
            it.putExtra("id", tagContent);
            it.putExtra("idusuario", idu);
            startActivity(it);


        }else{
            Toast.makeText(this, "Nenhuma gravação encontrada", Toast.LENGTH_SHORT).show();
        }


    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this, Main2Activity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter [] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {nfcAdapter.disableForegroundDispatch(this);}


                                                //PARTE DE ESCRITA ABAIXO \/
       private NdefRecord createTextRecord(String content){
        try{
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLenght = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLenght);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLenght);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e){
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }



        private NdefMessage createNdefMessage(String content){
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
        return ndefMessage;
    }


    public void tglReadWriteOnClick(View view){
        txtTagContent.setText("");
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){

        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize -1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }



}


