package modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by aluno on 30/09/2015.
 */
public class UsuarioService {

    private static final String TABELA_USUARIO = "usuario";
    private static final String ATRIBUTO_ID = "id";
    private static final String ATRIBUTO_NOME = "nome";
    private static final String ATRIBUTO_LOGIN = "login";
    private static final String ATRIBUTO_SENHA = "senha";

    private SQLiteDatabase database;

    public UsuarioService(SQLiteDatabase db) {
        database = db;

        StringBuilder sqlDB = new StringBuilder();

        sqlDB.append(" CREATE TABLE IF NOT EXISTS " + TABELA_USUARIO);

        sqlDB.append(" ( " + ATRIBUTO_ID + " integer primary key autoincrement, ");

        sqlDB.append(" " + ATRIBUTO_NOME + " varchar (50) not null,");

        sqlDB.append(" " + ATRIBUTO_LOGIN + " varchar (50) not null,");

        sqlDB.append(" " + ATRIBUTO_SENHA + " varchar(15) not null ) ");


        // Log.d("*****AQUIIIIIIIIIII", sqlDB.toString());
//
        database.execSQL(sqlDB.toString());
        //  testaMetodos();
        Log.d("AQUIIIIIIIIIII", sqlDB.toString());
    }

    public boolean inserirUsuario(Usuario e) {

        //chave valor, nome coluna e valor
        ContentValues cv = new ContentValues();
        cv.put(ATRIBUTO_NOME, e.getNome());
        cv.put(ATRIBUTO_LOGIN, e.getLogin());
        cv.put(ATRIBUTO_SENHA, e.getSenha());


        long retorno = database.insert(TABELA_USUARIO, ATRIBUTO_ID, cv);

        if (retorno > 0) {
            return true;
        } else {
            return false;
        }


    }

    public Usuario verificaLogin(String login, String senha) {

        String slq = "SELECT " + ATRIBUTO_LOGIN + "," + ATRIBUTO_SENHA + " FROM " + TABELA_USUARIO + " WHERE " + ATRIBUTO_LOGIN + " = '" + login + "' AND " + ATRIBUTO_SENHA + " = '" + senha + "'";

        Cursor cursor = database.rawQuery(slq, null);

        Usuario usuario = new Usuario(login, senha);
        if (cursor.moveToFirst()) {
            usuario.setLogin(cursor.getString(cursor.getColumnIndex(ATRIBUTO_LOGIN)));
            usuario.setSenha(cursor.getString(cursor.getColumnIndex(ATRIBUTO_SENHA)));
        }else{
            return null;
        }
        return usuario;
    }


    public boolean alterar(Usuario e){
        ContentValues cv = new ContentValues();
        cv.put(ATRIBUTO_NOME, e.getNome());
        cv.put(ATRIBUTO_LOGIN, e.getLogin());
        cv.put(ATRIBUTO_SENHA, e.getSenha());

        long retorno = database.update(TABELA_USUARIO, cv, ATRIBUTO_ID + "=" + e.getId(), null);

        if(retorno > 0){
            return  true;
        }
        else {
            return false;
        }

    }
}
