package iva.metroelki;

import java.io.*;
import java.net.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;


/**
 * Created by Илья Павлов on 25.11.2016.
 */

public class TrainSchedule {

    public JSONObject resultResponse;
    public JSONArray threads;

    public TrainSchedule() {
        //this.resultResponse = getHTML(httpsGet);
        //this.threads = (JSONArray)resultResponse.get("threads");
    }

    //Метод отправки GET запроса и получения от сервера JSON объекта
    public static JSONObject getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = new JSONObject();

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            jsonObj = (JSONObject) jsonParser.parse(rd);
            rd.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return jsonObj;
    }


    //Поиск 4 ближайших электричек, отталкиваясь от текущего времени
    public static ArrayList<String> getNearestDepTime (JSONArray arrayToParse, String curTime) {

        JSONObject obj = new JSONObject();
        int arraySize = arrayToParse.size();
        int start, end, curMinutes, depMinutes, curHours, depHours;
        String depTime, dTime;

        ArrayList<String> listOfdepTime = new ArrayList<String>();

        curMinutes = Integer.parseInt(curTime.substring(3,5));
        curHours = Integer.parseInt(curTime.substring(0,2));

        for(int i=0; i<=arraySize-1; i++) {
            obj = (JSONObject) arrayToParse.get(i);
            depTime = (String) obj.get("departure");
            start = depTime.indexOf(" ") + 1;
            end = depTime.lastIndexOf(":00");
            dTime = depTime.substring(start, end);
            depMinutes = Integer.parseInt(dTime.substring(3,5));
            depHours = Integer.parseInt(dTime.substring(0,2));


            if (curHours <= depHours && curMinutes < depMinutes)
                listOfdepTime.add(dTime);

            if(listOfdepTime.size() == 4) break;
        }

        return listOfdepTime;

    }


    //Поиск времени прибытия 4 электричек, полученных пердыдущем методом
    public static ArrayList<String> getArrivalTime(JSONArray arrayToParse, ArrayList<String> nearestDepTime) {
        ArrayList<String> listOfArrivalTime = new ArrayList<String>();
        int arraySize = arrayToParse.size();

        JSONObject obj1 = new JSONObject();

        String arrTime, aTime, depTime, dTime;
        int depStart, depEnd, arrStart, arrEnd, index;
        index = 0;

        for (int i = 0; i <= arraySize - 1; i++) {
            obj1 = (JSONObject) arrayToParse.get(i);
            depTime = (String) obj1.get("departure");
            depStart = depTime.indexOf(" ") + 1;
            depEnd = depTime.lastIndexOf(":00");
            dTime = depTime.substring(depStart, depEnd);

            if (Objects.equals(dTime, nearestDepTime.get(index))) {
                arrTime = (String) obj1.get("arrival");
                arrStart = arrTime.indexOf(" ") + 1;
                arrEnd = arrTime.lastIndexOf(":00");
                aTime = arrTime.substring(arrStart, arrEnd);
                listOfArrivalTime.add(aTime);
                ++index;
                if (index == nearestDepTime.size()) break;
            }
        }
        return listOfArrivalTime;
    }

    public static String getDate(int curYear, int curMonth, int curDay) {
        StringBuilder sb = new StringBuilder();
        sb.append(curYear + "-" + curMonth + "-" + curDay);
        return sb.toString();
    }

    public static String getTime(int curHours, int curMinutes) {
        StringBuilder sb = new StringBuilder();
        if(curHours >= 0 && curHours< 10 && curMinutes >= 0 && curMinutes< 10) sb.append("0" + curHours + ":" + "0" + curMinutes);
        else if (curHours >= 0 && curHours< 10 && curMinutes >= 10) sb.append("0" + curHours + ":" + curMinutes);
        else if (curHours >=10 && curMinutes >= 0 && curMinutes< 10) sb.append(curHours + ":" + "0" + curMinutes);
        else sb.append(curHours + ":" + curMinutes);
        return sb.toString();
    }



    public static String makingURL (String stationFrom, String stationTo) {

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(calendar.YEAR); // Текущий год
        int curMonth = calendar.get(calendar.MONTH) + 1; // Текущий месяц
        int curDay = calendar.get(calendar.DAY_OF_MONTH);
        String curDate = getDate(curYear, curMonth, curDay);

        String httpsGet = "https://api.rasp.yandex.net/v1.0/search/?apikey=99d5d526-8473-4b82-b937-6ada77d11889&format=json&from=" + stationFrom + "&to=" + stationTo + "&lang=en&page=1&date=" + curDate;

        return httpsGet;
    }





    //   public static JSONArray getJSON() {
    //  String stationFrom = "s9601862"; // Код станции отправления (Монино)
    // String stationTo = "s2000002"; // Код станции прибытия (Москва-Ярославская)

    // Calendar calendar = Calendar.getInstance(); // Создание календаря для получения точной даты
    //  int curYear = calendar.get(calendar.YEAR); // Текущий год
    //    int curMonth = calendar.get(calendar.MONTH) + 1; // Текущий месяц
    //  int curDay = calendar.get(calendar.DAY_OF_MONTH); // Текущий день

        /*ArrayList<String> listOfArrivaleTime = new ArrayList<String>();
        ArrayList<String> listOfTrainType = new ArrayList<String>();*/

    // String curDate = getDate(curYear, curMonth, curDay);
    //String curTime =  getTime(curHours,curMinutes);

    //Формирование запроса для получения расписания рейсов между станциями
    // String httpsGet = "https://api.rasp.yandex.net/v1.0/search/?apikey=99d5d526-8473-4b82-b937-6ada77d11889&format=json&from=" + stationFrom + "&to=" + stationTo + "&lang=en&page=1&date=" + curDate;

    // JSONObject resultJson = getHTML(httpsGet); // Получение JSON объекта от сервера API.Расписаний
    // JSONArray threads = (JSONArray) resultJson.get("threads"); // Создание массива из JSON объектов (1 объект - 1 рейс)

        /*ArrayList<String> nearestDepTime = new ArrayList<String>(getNearestDepTime(threads, curTime));

        nearestDepTime = getNearestDepTime(threads, curTime);*/

    //return threads;

            /*listOfArrivaleTime = getArrivalTime(threads, nearestDepTime);
            System.out.println(listOfArrivaleTime);*/
			 /*listOfTrainType = getTrainType(threads, nearestDepTime);
			 System.out.println(listOfTrainType); */

    // }
}