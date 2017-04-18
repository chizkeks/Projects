package iva.metroelki;

import android.database.Cursor;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by mister on 06.11.2016.
 */

public class MetroElkiMap {
    ArrayList<MetroLine> MetroMap;
MetroElkiMap(sqlitedbapp sqlHelper){
    this.MetroMap=new ArrayList<MetroLine>();
    // создаем базу данных
    sqlHelper.onUpgrade(null,0,1);
    try {
        //открываем бд
        sqlHelper.open();
        //заброс на содержимое таблицы метро
        Cursor UserCursor= sqlHelper.database.rawQuery("SELECT * FROM Metro",null);
        System.out.println("size db  "+UserCursor.getCount());
                UserCursor=sqlHelper.database.rawQuery("SELECT DISTINCT MetroLineID FROM Metro",null);
        System.out.println("first "+UserCursor.getCount());
       UserCursor.moveToFirst();
        System.out.println(UserCursor.getInt(0));
       /* UserCursor.moveToNext();
        System.out.println(UserCursor.getInt(0));*/
        int count_lines=UserCursor.getCount();
        UserCursor.moveToFirst();
        for(int i=0;i<count_lines;i++){
            System.out.println("second");
           MetroLine tmp=new MetroLine();
            tmp.Id_line=UserCursor.getInt(0);
            this.MetroMap.add(tmp);
            UserCursor.moveToNext();
        }
        for(int i=0;i<this.MetroMap.size();i++){
            UserCursor=sqlHelper.database.rawQuery("SELECT * FROM Metro WHERE MetroLineID="+this.MetroMap.get(i).Id_line,null);
            //заполняем линию станциями
            UserCursor.moveToFirst();
            this.MetroMap.get(i).name_line=UserCursor.getString(1);
            int count_station_of_line=UserCursor.getCount();
            this.MetroMap.get(i).stations=new ArrayList<metro_station>();
            for(int j=0;j<count_station_of_line;j++){

                metro_station tmp=new metro_station();
                tmp.id_station=UserCursor.getInt(2);
                tmp.name=UserCursor.getString(3);
                tmp.time_to_next_station=UserCursor.getInt(4);
                tmp.time_to_prev_station=UserCursor.getInt(5);
                this.MetroMap.get(i).stations.add(tmp);
                UserCursor.moveToNext();
            }
        }


    //заполняем пересадками карту
        UserCursor=sqlHelper.database.rawQuery("SELECT * FROM Peresadki",null);
        UserCursor.moveToFirst();
        for(int i=0;i<UserCursor.getCount();i++){
         peresadka tmp=new peresadka();
            peresadka tmp2=new peresadka();
            tmp.id_first_station=UserCursor.getInt(2);
            tmp2.id_second_station=tmp.id_first_station;
            tmp.id_second_station=UserCursor.getInt(3);
            tmp2.id_first_station=tmp.id_second_station;
            tmp.name_first_station=UserCursor.getString(4);
            tmp2.name_second_station=tmp.name_first_station;
            tmp.name_second_station=UserCursor.getString(5);
            tmp2.name_first_station=tmp.name_second_station;
            tmp.time=UserCursor.getInt(6);
            tmp2.time=tmp.time;
            tmp.first_parent=this.MetroMap.get(UserCursor.getInt(0)-1);
            tmp2.second_parent=tmp.first_parent;
            tmp.second_parent=this.MetroMap.get(UserCursor.getInt(1)-1);
            tmp2.first_parent=tmp.second_parent;
            this.MetroMap.get(UserCursor.getInt(0)-1).peres_of_line.add(tmp);
            this.MetroMap.get(UserCursor.getInt(1)-1).peres_of_line.add(tmp2);

            UserCursor.moveToNext();

        }

        UserCursor.close();
    } catch (SQLException e) {
        System.out.println("errror!!!!");  e.printStackTrace();
    }
    sqlHelper.close();
}
  public Way find_ways(sqlitedbapp sqlHelper,String first_station,String second_station){

      ArrayList<Way> t_array=new ArrayList<Way>();
      int id_s1=0,id_s2=0,id_l1=0,id_l2=0;
      try {
          //открываем бд
          sqlHelper.open();
          //запрос на содержимое таблицы метро
          Cursor UserCursor= sqlHelper.database.rawQuery("SELECT * FROM Metro WHERE MetroStationName = '"+first_station+"'",null);
          UserCursor.moveToFirst();
          id_l1=UserCursor.getInt(0);
          id_s1=UserCursor.getInt(2);
          UserCursor= sqlHelper.database.rawQuery("SELECT * FROM Metro WHERE MetroStationName='"+second_station+"'",null);
          UserCursor.moveToFirst();
          id_l2=UserCursor.getInt(0);
          id_s2=UserCursor.getInt(2);
          UserCursor.close();
  } catch (SQLException e) {
        System.out.println("errror!!!!");  e.printStackTrace();
    }
      Way tmp=new Way();

      System.out.println("first and second station"+id_l1+" "+id_s1+" "+id_l2+" "+id_s2);
      if(id_l1==id_l2){// если станции на одной линии

          Step stp1=new Step();
          stp1.id_first=id_s1;
          stp1.id_line=id_l1;
          stp1.id_second=id_s2;
          stp1.time=this.MetroMap.get(id_l1-1).time_of_way_on_line(stp1.id_first,stp1.id_second);
          tmp.steps.add(stp1);
          t_array.add(tmp);
          return t_array.get(0);
      }
      //иначе
      for(int i=0;i<this.MetroMap.get(id_l1-1).peres_of_line.size();i++){//перебор по всем пересадкам линии
          if(this.MetroMap.get(id_l1-1).peres_of_line.get(i).second_parent.Id_line==id_l2){
              //поиск с 1 пересадкой
              System.out.println("nashel s odnoy peresadkoy");
              Way tmp1=new Way();
              Step stp1=new Step();
              stp1.id_first=id_s1;
              stp1.id_line=id_l1;
              stp1.id_second=this.MetroMap.get(id_l1-1).peres_of_line.get(i).id_first_station;
              stp1.time=this.MetroMap.get(id_l1-1).time_of_way_on_line(stp1.id_first,stp1.id_second);
              tmp1.steps.add(stp1);
              tmp1.peresadki.add(this.MetroMap.get(id_l1-1).peres_of_line.get(i));
              Step stp2=new Step();
              stp2.id_first=this.MetroMap.get(id_l1-1).peres_of_line.get(i).id_second_station;
              stp2.id_line=id_l2;
              stp2.id_second=id_s2;
              stp2.time=this.MetroMap.get(id_l2-1).time_of_way_on_line(stp2.id_first,stp2.id_second);
              tmp1.steps.add(stp2);
              System.out.println("dobavil s 1 peres");
              t_array.add(tmp1);
          } else{
          //так же смотрим с двумя пересадками
          for(int i2=0;i2<this.MetroMap.get(id_l1-1).peres_of_line.get(i).second_parent.peres_of_line.size();i2++){
              //перебор вторых пересадок
              if(this.MetroMap.get(id_l1-1).peres_of_line.get(i).second_parent.peres_of_line.get(i2).second_parent.Id_line==id_l2){
                 //по первой линии до пересадки
                  Way tmp2=new Way();
                  Step stp1=new Step();
                  stp1.id_first=id_s1;
                  stp1.id_line=id_l1;
                  stp1.id_second=this.MetroMap.get(id_l1-1).peres_of_line.get(i).id_first_station;
                  stp1.time=this.MetroMap.get(id_l1-1).time_of_way_on_line(stp1.id_first,stp1.id_second);
                  tmp2.steps.add(stp1);
                  //по второй линии до пересадки
                  tmp2.peresadki.add(this.MetroMap.get(id_l1-1).peres_of_line.get(i));
                  Step stp2=new Step();
                  stp2.id_first=this.MetroMap.get(id_l1-1).peres_of_line.get(i).id_second_station;
                  stp2.id_line=this.MetroMap.get(id_l1-1).peres_of_line.get(i).second_parent.Id_line;
                  stp2.id_second=this.MetroMap.get(stp2.id_line-1).peres_of_line.get(i2).id_first_station;
                  stp2.time=this.MetroMap.get(stp2.id_line-1).time_of_way_on_line(stp2.id_first,stp2.id_second);
                  tmp2.steps.add(stp2);
                  //по третей линии
                  System.out.println(i2);
                  tmp2.peresadki.add(this.MetroMap.get(id_l1-1).peres_of_line.get(i).second_parent.peres_of_line.get(i2));
                  Step stp3=new Step();
                  stp3.id_first=this.MetroMap.get(stp2.id_line-1).peres_of_line.get(i2).id_second_station;
                  stp3.id_line=id_l2;
                  stp3.id_second=id_s2;
                  stp3.time=this.MetroMap.get(stp3.id_line-1).time_of_way_on_line(stp3.id_first,stp3.id_second);
                  tmp2.steps.add(stp3);
                  System.out.println("dobavil s 2 peres");
                  t_array.add(tmp2);
              }
          }}
      }
     System.out.println("kol-vo ways "+t_array.size());
      int res_time=0;
      int res_way=0;
      for(int g=0;g<t_array.size();g++){
          int time_w=0;
          for (int g1=0;g1<t_array.get(g).steps.size();g1++){
              time_w+=t_array.get(g).steps.get(g1).time;
          }
          for(int g1=0;g1<t_array.get(g).peresadki.size();g1++){
              time_w+=t_array.get(g).peresadki.get(g1).time;
          }
          if(g==0||time_w<res_time){
              res_time=time_w;
              res_way=g;
          }
      }
    sqlHelper.close();
      System.out.println("time= "+res_time);
     return t_array.get(res_way);}
}

class MetroLine{
    int Id_line;
    String name_line;
    ArrayList<metro_station> stations;
    ArrayList<peresadka> peres_of_line;
    MetroLine(){
this.peres_of_line=new ArrayList<peresadka>();
    }

    public int time_of_way_on_line(int first_id,int second_id){ System.out.println("id1 "+first_id+" id2 "+second_id+" line "+this.Id_line);
first_id--;
        second_id--;
        int time=0;
        if(first_id==second_id){return time;}else {
        if((first_id<second_id)&&(this.Id_line!=3)){
           for(int i=first_id;i<second_id;i++){System.out.println("=="+i); time+=this.stations.get(i).time_to_next_station;} return time;
        }else {
            if((first_id>second_id)&&(this.Id_line!=3)){
                for(int i=first_id;i>second_id;i--){time+=this.stations.get(i).time_to_prev_station;} return time;
            }else {if(this.Id_line==3){
                int time1=0;
                int time2=0;
                if(first_id<second_id){
                    for(int i=first_id;i<second_id;i++){time1+=this.stations.get(i).time_to_next_station;}
                    for(int i=first_id;i!=second_id;i--){System.out.println(this.stations.get(i).name+" "+this.stations.get(i).time_to_prev_station);time2+=this.stations.get(i).time_to_prev_station; if(i==0){i=this.stations.size();}}
                }else {
                    if(first_id>second_id){
                        for(int i=first_id;i>second_id;i--){time1+=this.stations.get(i).time_to_prev_station;}
                        for(int i=first_id;i!=second_id;i++){time2+=this.stations.get(i).time_to_next_station; if(i==this.stations.size()-1){i=-1;}}
                    }
            }
            if(time1<time2){return time1;}else return time2;


            }
        }}
    }
        return 0;
}

}



class metro_station{
    int id_station;
    String name;
    ArrayList<peresadka> Peresadki;
    int time_to_next_station;
    int time_to_prev_station;
}

class peresadka{
    MetroLine first_parent,second_parent;
    String name_first_station;
    int id_first_station;
    String name_second_station;
    int id_second_station;
    int time;
}
class Way{
    Way(){this.steps=new ArrayList<Step>();
    this.peresadki=new ArrayList<peresadka>();}
    int Time;
    ArrayList<Step> steps;
    ArrayList<peresadka> peresadki;

}


class Step{
    int time;
    int id_line;
    int id_first,id_second;
}