package model;

import java.util.HashMap;
import java.util.Map.Entry;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import auxiliar.ApiRequests;
import inferface.I_Acceso_A_Datos;

public class ApiNodeJSManager implements I_Acceso_A_Datos {

	ApiRequests encargadoPeticiones;
	private String SERVER_PATH, GET, SET_insert_USUARIO, SET_delete_USUARIO, SET_deleteAll_USUARIOS;
	HashMap<Integer, Users> listapi;
	int count;
	JSONObject objUser;
	JSONObject objRequest;
	URL url;

	public ApiNodeJSManager() {
		listapi = new HashMap<Integer, Users>();
		encargadoPeticiones = new ApiRequests();
		SERVER_PATH = "http://127.0.0.1:8081/";

	}

	@Override
	public HashMap<Integer, Users> leer() {

		GET = "api/users";

		try {

			System.out.println("---------- Leemos datos de JSON --------------------");

			String url = SERVER_PATH + GET; // Sacadas de configuracion

			System.out.println("La url a la que lanzamos la peticion es " + url);

			String response = encargadoPeticiones.getRequest(url);

			JSONObject respuesta = (JSONObject) JSONValue.parse(response);
			JSONArray users = (JSONArray) respuesta.get("users");

			for (Object object : users) {
				JSONObject json = (JSONObject) object;

				count++;
				String db_username = json.get("username").toString();
				String db_password = json.get("password").toString();
				String db_description = json.get("description").toString();

				Users usu = new Users(db_username, db_password, db_description);

				listapi.put(count, usu);

			}
			count = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listapi;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean insertusu(Users usu) {
		boolean state = false;
		SET_insert_USUARIO = "api/users";

		objUser = new JSONObject();
		objRequest = new JSONObject();
		try {
			listapi.put(count++, usu);
			objUser.put("username", usu.getUsername());
			objUser.put("password", usu.getPassword());
			objUser.put("description", usu.getDescription());

			objRequest.put("request", "add");
			objRequest.put("userAdd", objUser);
			String json = objRequest.toJSONString();

			String url = SERVER_PATH + SET_insert_USUARIO;

			// System.out.println("La url a la que lanzamos la peticiÛn es " + url);
			// System.out.println("El json que enviamos es: ");
			// System.out.println(json);

			String response = encargadoPeticiones.postRequest(url, json);

			// System.out.println("El json que recibimos es: ");

			// System.out.println(response);

			// Parseamos la respuesta y la convertimos en un JSONObject
			JSONObject respuesta = (JSONObject) JSONValue.parse(response.toString());

			if (respuesta == null) {
				System.out.println("El json recibido no es correcto. Finaliza la ejecucion");
				System.exit(0);
			} else { // El JSON recibido es correcto

				String stateJSON = (String) respuesta.get("state");
				if (stateJSON.equals("ok")) {

					state = true;
					System.out.println("Insertado");

				} else { // Hemos recibido el json pero en el state nos
							// indica que ha habido algun error

					System.out.println("Acceso JSON REMOTO - Error al almacenar los datos");
					System.out.println("Error: " + (String) respuesta.get("error"));
					System.out.println("Consulta: " + (String) respuesta.get("query"));

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean update(String up_username, String newPassword, String newDescription) {
		boolean state = false;
		SET_insert_USUARIO = "api/users/"+up_username;
		objUser = new JSONObject();
		objRequest = new JSONObject();
		try {
			objUser.put("username", up_username);
			objUser.put("password", newPassword);
			objUser.put("description", newDescription);

			objRequest.put("request", "upd");
			objRequest.put("userUpd", objUser);
			String json = objRequest.toJSONString();

			String url = SERVER_PATH + SET_insert_USUARIO;

			// System.out.println("El json que enviamos es: ");
			// System.out.println(json);

			String response = encargadoPeticiones.putRequest(url, json);

			// System.out.println("El json que recibimos es: ");

			// System.out.println(response);

			// Parseamos la respuesta y la convertimos en un JSONObject
			JSONObject respuesta = (JSONObject) JSONValue.parse(response.toString());

			if (respuesta == null) {
				System.out.println("El json recibido no es correcto. Finaliza la ejecuciÛn");

			} else { // El JSON recibido es correcto

				// Sera "ok" si todo ha ido bien o "error" si hay algun problema
				String stateJSON = (String) respuesta.get("state");
				if (stateJSON.equals("ok")) {
					state = true;
					System.out.println("Almacenado " + up_username + " con los nuevos registros introducidos");

				} else { // Hemos recibido el json pero en el estado nos
							// indica que ha habido algun error

					System.out.println("Acceso JSON REMOTO - Error al almacenar los datos");
					System.out.println("Error: " + (String) respuesta.get("error"));
					System.out.println("Consulta: " + (String) respuesta.get("query"));

					System.exit(-1);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public boolean deleteone(String del_username) {
		boolean state = false;
		SET_delete_USUARIO = "api/users/"+del_username;
		objUser = new JSONObject();
		objRequest = new JSONObject();
		try {
			objUser.put("username", del_username);

			listapi.remove(del_username);

			objRequest.put("request", "del");
			objRequest.put("userDel", objUser);

			String json = objRequest.toJSONString();

			// peticion para borrar usuaerio
			String url = SERVER_PATH + SET_delete_USUARIO;

			String response = encargadoPeticiones.deleteRequest(url, json);

			// Parseamos la respuesta y la convertimos en un JSONObject
			JSONObject respuesta = (JSONObject) JSONValue.parse(response.toString());

			if (respuesta == null) {
				System.out.println("El json recibido no es correcto. Finaliza la ejecuciÛn");
				state = false;

			} else { // El JSON recibido es correcto

				// Sera "ok" si todo ha ido bien o "error" si hay algun problema
				String stateJSON = (String) respuesta.get("state");
				if (stateJSON.equals("ok")) {

					System.out.println(del_username + " Borrado correctamente");
					state = true;
				} else {
					// error
					System.out.println("Acceso JSON REMOTO - Error al almacenar los datos");
					System.out.println("Error: " + (String) respuesta.get("error"));
					System.out.println("Consulta: " + (String) respuesta.get("query"));
					state = false;

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}

	@Override
	public boolean deleteall(String option) {
		boolean state = false;
		if (option.equalsIgnoreCase("si")) {

			SET_deleteAll_USUARIOS = "api/users";

			try {
				url = new URL(SERVER_PATH + SET_deleteAll_USUARIOS);
				URLConnection con = url.openConnection();
				con.getInputStream(); // retornamos la entrada de la conexion abierta con el PHP
			} catch (IOException e) {
				e.printStackTrace();
				state = false;
			}
			state = true;
		}
		return state;
	}

	@Override
	public void intercambiodatoslist(HashMap<Integer, Users> newList) {
		deleteall("si");
		for (Entry<Integer, Users> entry : newList.entrySet()) {
			insertusu(newList.get(entry.getKey()));
		}
		System.out.println("INTERCAMBIO DE PHP-JSON CORRECTO");
	}

}