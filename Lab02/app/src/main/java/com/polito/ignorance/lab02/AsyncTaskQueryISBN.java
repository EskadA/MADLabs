package com.polito.ignorance.lab02;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Giulio on 12/04/2018.
 */

public class AsyncTaskQueryISBN extends AsyncTask <String,Integer,String>
{
    //<> arguments: 1)do in background input (for ex String to put in the isbn query)
    //              2)Data type of onProgressUpdate
    //              3) Data type of the result to pass to Principal UI, is the same type returned by doInBsckground (because is the same value)




    /*The AsyncTask will need to update the TextView once it has completed sleeping. The constructor will then need to include the TextView, so that it can be updated in onPostExecute().
    Define a member variable mTextView.
    Implement a constructor for AsyncTask that takes a TextView and sets mTextView to the one passed in TextView:
     public AsyncTaskQueryISBN(TextView tv) {mTextView = tv;}*/

    EditText authorEditText;
    EditText titleEditText;
    EditText publisherEditText;
    EditText publishedYearEditText;

    //Costruttore
    //Viene chiamato nel main thread, Usa la text view associata nel main
    public AsyncTaskQueryISBN(EditText aet, EditText tet, EditText pet, EditText pyet) {authorEditText = aet; titleEditText=tet; publisherEditText=pet; publishedYearEditText=pyet;}

    @Override
    //the method makes a query to googleapis using the isbn obtained with the scan -> passed as String argument
    protected String doInBackground(String... strings) {

        //I take the isbn using which i will download book info from google api
        //using the get request "https://www.googleapis.com/books/v1/volumes?q=isbn:<your_isbn_here>"
        //Android Developer fundamental 7.2: Connect to the Internet


                                                            /*Before your app can make network calls, you need to include a permission in your AndroidManifest.xml file. Add the
                                                            following tag inside the <manifest> tag:
                                                            <uses-permission android:name="android.permission.INTERNET" />

                                                            When using the network, it's a best practice to monitor the network state of the device so that you don't attempt to make
                                                            network calls when the network is unavailable. To access the network state of the device, your app needs an additional
                                                            permission:
                                                            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />*/



                                                            /*Always perform network operations on a worker thread, separate from the UI. For example, in your Java code you could
                                                                create an AsyncTask (or AsyncTaskLoader ) implementation that opens a network connection and queries an API. Your
                                                                main code checks whether a network connection is active. If so, it runs the AsyncTask in a separate thread, then displays
                                                                the results in the UI.
                                                                Note: If you run network operations on the main thread instead of on a worker thread, you receive an error.*/


        //Building the URI
        String ISBN = strings[0]; //acquisition from the passed parameter
        String myurl = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+ISBN;
        Log.d("debug",myurl);

        //Now you have to inoltrate an http get request.
        //This has to be done not by the main thread (this will cause an error)
        //But using another worker thread (async
        InputStream inputStream = null;
        int len = 240000;//la lunghezza della rispolta è grande, seimposto len troppo basso avrò l'eccezione rilevata in precedenza
        URL url = null; //defined and initialized here to be visible to "finally" block
        HttpURLConnection conn = null;   //thorws IOException in case of error
        String responseString = null;


        try {
            //Open connession to the url and set connection parameters
            url = new URL(myurl);
            conn = (HttpURLConnection) url.openConnection();
            Log.d("Tag", "Debug1");

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            Log.d("Tag", "Debug2");


            //Start the query -> there is an error probably PERMISSION (SOLVED)
            conn.connect(); //attacco alla connessione aperta
            Log.d("Tag", "Debug3");


            int response = conn.getResponseCode(); //acquisisce il codice ritornato dal server
            Log.d("tag", "The response is: " + response);

            inputStream = conn.getInputStream();   //prende la risposta (solitamente pagina formato JSON) come InputStream( stream di bytes))
            Log.d("Tag", "Debug5");

            responseString = convertInputToString(inputStream, len);
            Log.d("Tag", "Convertito input stream");

            // Close the InputStream and connection
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); }

        finally {
            conn.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            // Convert the InputStream into a string
Log.d("tag", responseString);
            return responseString; //ritorna la pagina scaricata sottoforma di Stringa -> valore che sarà dato in input a onPostExecute
        }
    }

      /*When you make web API queries, the results are often in JSON format.
        Below is an example of a JSON response from an HTTP request. It shows the names of three menu items in a popup menu


        {"menu":
            {
                "id": "file",
                "value": "File",
                "popup": {
                "menuitem": [
                                {"value": "New", "onclick": "CreateNewDoc()"},
                                {"value": "Open", "onclick": "OpenDoc()"},
                                {"value": "Close", "onclick": "CloseDoc()"}
                            ]
            }
        }
            Object>     Array(id,value,popup,menuitem)      >Object(value,onclick)

        HOW TO READ JSON
            Element :{    is an object
            Element :[    is an array
                            internal to array there are other objects---> array.getJsonObject(index)
            {/{ "key":"value" is a string -> GetJsonString("key")



        In questo fragente il riultato per un libro sarà:
            {
                 "kind": "books#volumes",
                 "totalItems": 1,
                 "items": [
                  {
                   "kind": "books#volume",
                   "id": "j1tuswEACAAJ",
                   "etag": "E951gpU8gRA",
                   "selfLink": "https://www.googleapis.com/books/v1/volumes/j1tuswEACAAJ",
                   "volumeInfo": {
                    "title": "Lord of the Flies",
                    "authors": [
                     "William Golding"
                    ],
                    "publisher": "Listening Library",
                    "publishedDate": "1984-06",
                    "description": "Classic novel by a Nobel prize winner about a group of boys who, after a plane crash, set up a primitive society on an uninhabited island. Vietnamese translation by Le Chu Cau. In Vietnamese. Distributed by Tsai Fong Books, Inc.",
                    "industryIdentifiers": [
                     {
                      "type": "ISBN_10",
                      "identifier": "0807218189"
                     },
                     {
                      "type": "ISBN_13",
                      "identifier": "9780807218181"
                     }
                    ],
                    "readingModes": {
                     "text": false,
                     "image": false
                    },
                    "printType": "BOOK",
                    "categories": [
                     "Fiction"
                    ],
                    "averageRating": 3.5,
                    "ratingsCount": 3102,
                    .
                    .
                    .
                    .
                    .

If i need title and author, i will use
JSONObject bookData = new JSONObject(responseString);
JSONArray items = data.getJSONArray("items");
JSONObject generalInfo  = menuItemArray.getJSONObject(0);
String title = generalInfo.getString("title");
String author = generalIngo.getString("author");
        */

    @Override
    protected void onPostExecute(String responseString)
    {
        JSONObject bookData=null;
        JSONArray items=null;
        JSONObject firstitem=null;
        JSONObject volumeInfo=null;
        String title=null;
        JSONArray authors=null;
        String author = null;
        String publisher = null;
        String publishedDate = null;

      try {


          //Dà l'errore qui, va vito meglio come navigare nel Json
          //W/System.err: org.json.JSONException: Unterminated string at character 500 -> Probabilmente la stringa non è abbastanza grande per la dimensione del file (Ci sono pagine che me la dann anche con len enorme)
          try
          {
              bookData = new JSONObject(responseString);
          }
          catch (JSONException e) {e.printStackTrace();}
          Log.d("tag"," bookData is "+bookData.toString());


          try
          {
              items = bookData.getJSONArray("items");
          }
          catch (JSONException e) {e.printStackTrace();}
          Log.d("tag"," item is "+items.toString());

          try
          {
              firstitem = items.getJSONObject(0);
          }
          catch (JSONException e) {e.printStackTrace();}
          Log.d("tag"," firstitem is "+firstitem);


          try
          {
              volumeInfo=firstitem.getJSONObject("volumeInfo");
          }
          catch (JSONException e) {e.printStackTrace();}
          Log.d("tag"," volumeinfo is "+volumeInfo);


          try
          {
              title = volumeInfo.getString("title");
          }
          catch (JSONException e) {e.printStackTrace(); title="Not Found";}
          Log.d("tag"," title is "+title);

          try
          {
              authors = volumeInfo.getJSONArray("authors");
              author = authors.getString(0);
              Log.d("tag", " author is " + author);
          }
          catch (JSONException e) {e.printStackTrace(); author="Not Found";}


          try
          {
              publishedDate = volumeInfo.getString("publishedDate");
          }
          catch (JSONException e) {e.printStackTrace(); publishedDate= "Not Found";}
          Log.d("tag"," publishedDate is "+publishedDate);


          try
          {
              publisher = volumeInfo.getString("publisher");
          }
          catch (JSONException e) {e.printStackTrace(); publisher="Not Found";}
          Log.d("tag"," publisher is "+publisher);





          //Note: You can update the UI in onPostExecute() because it is run on the main (UI) thread. You cannot call mTextView.setText() in doInBackground(),
            // because that method is executed on a separate thread.
           }


    finally {
            titleEditText.setText(title);
            authorEditText.setText(author);
            publisherEditText.setText(publisher);
            publishedYearEditText.setText(publishedDate);
        }

    }







    // Reads an InputStream and converts it to a String.
    public String convertInputToString(InputStream stream, int len)
             {

        //Note: If you expect a long response, wrap your InputStreamReader inside a BufferedReader for more efficient reading of
        //characters, arrays, and lines. For example:

        Reader reader = null;
                 try {
                     reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                 } catch (UnsupportedEncodingException e) {
                     e.printStackTrace();
                 }
                 Log.d("Tag", "Debug4.1");
        char[] buffer = new char[len];
                 try {
                     reader.read(buffer);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 return new String(buffer);
    }



}

