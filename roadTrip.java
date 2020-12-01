  
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class roadTrip {
    /*
    These are the data members of the roadTrip class
     */
    ArrayList<Routes> routes=new ArrayList<Routes>(522);
    HashMap<String,String> attraction=new HashMap<String, String>(145);
    HashMap<String,Boolean> beenThere=new HashMap<String, Boolean>(522);
    HashMap<String,Integer> miles=new HashMap<String,Integer>(522);
    HashMap<String,String> travel=new HashMap<String, String>(522);
    HashMap<String, Boolean> realCity=new HashMap<String, Boolean>(522);
    ArrayList<String> cities=new ArrayList<String>(1000);
    Stack<String> stack=new Stack<String>();
    int mile=0;
    ArrayList<Integer> check=new ArrayList<Integer>(522);
    /*
    This is the routes object meant to store data from the roads.csv file with four
    data members and a constructor initializing all those data members
     */
    public class Routes {
        String location1;
        String location2;
        int miles;
        int minutes;
        public Routes(String loc1,String loc2,int m, int mins){
            location1=loc1;
            location2=loc2;
            miles=m;
            minutes=mins;
        }
    }
    /*
    parseFiles reads both roads.csv and attractions.csv-- stores the contents
    of roads.csv into an arrayList and attractions.csv stores all the attractions
    and its cities into a hashmap 
     */
    public void parseFiles(String file1, String file2){
        try{
            BufferedReader buffRead = new BufferedReader(new FileReader(file1));
            String line="";
            while((line=buffRead.readLine())!=null){
                String[] temp=line.split(",");
                Routes r=new Routes(temp[0].toLowerCase(),temp[1].toLowerCase(),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]));
                realCity.put(temp[0].toLowerCase(),true);
                realCity.put(temp[1].toLowerCase(),true);
                routes.add(r);
            }
            buffRead=new BufferedReader(new FileReader(file2));
            while((line=buffRead.readLine())!=null){
                String[] temp=line.split(",");
                attraction.put(temp[0].toLowerCase(),temp[1].toLowerCase());
            }
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("The file had an error, the system will now exit.");
            System.exit(1);
        }
    }
    /*
    route is the main method which will keep calling newMap and find the fastest
    route from the starting city to all the attractions. It will locate the closest place,
    go to that location and store the route, then from that attraction it will find the new nearest location
    and store that route until there are no more locations. Lastly it will store the route from the last location
    to the ending_city
     */
    public List<String> route(String starting_city, String ending_city, List<String> attractions){
        //Error Catching
        starting_city=starting_city.toLowerCase();
        ending_city=ending_city.toLowerCase();
        for(int i=0;i<attractions.size();i++){
            attractions.set(i,attractions.get(i).toLowerCase());
        }
        if(realCity.get(starting_city)==null) {
            System.out.println(starting_city+" cannot be found in the roads.csv file.");
            return cities;
        }
        if(realCity.get(ending_city)==null) {
            System.out.println(ending_city+" cannot be found in the roads.csv file.");
            return cities;
        }
        boolean truth=false;
        for(int i=0;i<attractions.size();i++){
            if(attraction.get(attractions.get(i))==null){
                System.out.println(attractions.get(i)+" cannot be found in the attractions.csv file.");
                truth=true;
            }
        }
        if(truth)
            return cities;
        newMap(starting_city);
        String temp=starting_city;
        while(!(attractions.isEmpty())){
            int t=miles.get(attraction.get(attractions.get(0)));
            int index=0;
            for(int i=0;i<attractions.size();i++){
                if(t>miles.get(attraction.get(attractions.get(i)))){
                    index=i;
                    t=miles.get(attraction.get(attractions.get(i)));
                }
            }
            tester(temp,travel.get(attraction.get(attractions.get(index))),attraction.get(attractions.get(index)));
            temp=attraction.get(attractions.get(index));
            beenThere=new HashMap<String, Boolean>(522);;
            miles=new HashMap<String,Integer>(522);;
            travel=new HashMap<String, String>(522);
            check=new ArrayList<Integer>(522);
            newMap(temp);
            attractions.remove(index);
        }
        tester(temp,ending_city,ending_city);

        return cities;
    }
    /*
    The method newMap will take a city and find the most efficient route to
    every other city using Dijkstras Algorithm. The method will also keep track of
    the previous city as it goes on and can keep getting updated if a more efficient
    route is found and will keep setting visited cities to false, the loop or method
    will end once the stack become empty.
     */
    public void newMap(String city) {
        int w=0;
        stack.add(city);
        miles.put(city, 0);
        travel.put(null, city);
        while (!(stack.isEmpty())) {
            String temp = stack.pop();
            beenThere.put(temp, false);
            for (int i = 0; i < routes.size(); i++) {
                if (routes.get(i).location1.equals(temp) || routes.get(i).location2.equals(temp)) {
                    if (routes.get(i).location1.equals(temp)&&!(check.contains(i))) {
                        if (beenThere.get(routes.get(i).location2) == null && !(stack.contains(routes.get(i).location2))) {
                            w++;
                            stack.add(routes.get(i).location2);
                            travel.put(routes.get(i).location2, temp);
                            miles.put(routes.get(i).location2, routes.get(i).miles + miles.get(temp));
                        }
                        else if (miles.get(temp)+routes.get(i).miles<miles.get(routes.get(i).location2)) {
                            travel.put(routes.get(i).location2, temp);
                            miles.put(routes.get(i).location2, routes.get(i).miles + miles.get(temp));
                        }
                        check.add(i);
                    }
                    else if (routes.get(i).location2.equals(temp)&&!(check.contains(i))) {
                        if (beenThere.get(routes.get(i).location1) == null && !(stack.contains(routes.get(i).location1))) {
                            w++;
                            stack.add(routes.get(i).location1);
                            travel.put(routes.get(i).location1, temp);
                            miles.put(routes.get(i).location1, routes.get(i).miles + miles.get(temp));
                        }
                        else if (miles.get(temp)+routes.get(i).miles<miles.get(routes.get(i).location1)) {
                            travel.put(routes.get(i).location1, temp);
                            miles.put(routes.get(i).location1, routes.get(i).miles + miles.get(temp));
                        }
                        check.add(i);
                    }
                }
            }
            String[] arr=new String[1000];
            int counter=0;
            while(!(stack.isEmpty())){
                arr[counter]=stack.pop();
                counter++;
            }
            for(int i=0;i<counter;i++){
                boolean truth=false;
                int m=miles.get(arr[i]);
                int index=i;
                for(int j=i;j<counter;j++){
                    if(m<miles.get(arr[j])){
                        m=miles.get(arr[j]);
                        index=j;
                        truth=true;
                    }
                }
                if(truth==true){
                    String t=arr[i];
                    arr[i]=arr[index];
                    arr[index]=t;
                }
            }
            for(int i=0;i<counter;i++)
                stack.add(arr[i]);
            w=0;
        }
    }
    /*
    The tester function adds the cities visited in order
    in the cities arrayList and also keeps track of the total miles
    traveled from one location to another and keeps accumulating it,
     */
    public void tester(String city, String city2, String city3){
        newMap(city);
        ArrayList<String> temp=new ArrayList<>();
        temp.add(city2);
        String t=city2;
        mile+=miles.get(city3);
        while(travel.get(t)!=null){
            t=travel.get(t);
            temp.add(t);
        }
        for(int i=0;i<temp.size();i++){
            cities.add(temp.get(temp.size()-i-1));
        }

    }
    public static void main(String[] args){
        /*
        This is the main method, which creates the map and Array List (which stores the attractions), 
        takes in the parameters which are the starting and ending point, and parses the files.
         */
        roadTrip map=new roadTrip();
        ArrayList<String> att=new ArrayList<>();
        
        //failed attempt to take user input
        // Scanner input = new Scanner(System.in);  // Create a Scanner object
        // System.out.println("How many stops would you like to make?");
        // int stops = input.nextInt();

        // for (int i = 0; i < stops + 1; i++) {
        //     System.out.println("Where would you like to stop? Please type exactly as the csv file.");
        //     String attraction = input.nextLine();
        //     att.add(attraction);} 
        

        att.add("Alcatraz");
        att.add("Pike Place Market");
        att.add("Nashville Walking Tour");
        att.add("Myrtle Beach");
        map.parseFiles("roads.csv","attractions.csv");
        System.out.println("The route in order: ");
        
        System.out.println(map.route("Seattle WA","Rochester NY",att));
        System.out.println("Total miles traveled: "+map.mile);
    }
}