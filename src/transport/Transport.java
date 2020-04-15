package transport;

import java.io.File;
import java.util.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;
 
public class Transport {
 
    private static int[] demand;
    private static int[] supply;
    private static double[][] costs;
    private static Shipment[][] matrix;
 
    private static class Shipment {
        final double costPerUnit;
        final int r, c;
        double quantity;
 
        public Shipment(double q, double cpu, int r, int c) {
            quantity = q;
            costPerUnit = cpu;
            this.r = r;
            this.c = c;
        }
    }
 
    static void init(String filename) throws Exception { 
        try (Scanner sc = new Scanner(new File(filename))) {
            int numSources = sc.nextInt();
            int numDestinations = sc.nextInt();
 
            List<Integer> src = new ArrayList<>();
            List<Integer> dst = new ArrayList<>();
 
            for (int i = 0; i < numSources; i++)
                src.add(sc.nextInt());
 
            for (int i = 0; i < numDestinations; i++)
                dst.add(sc.nextInt());
 
            // fix imbalance
            int totalSrc = src.stream().mapToInt(i -> i).sum();
            int totalDst = dst.stream().mapToInt(i -> i).sum();
            if (totalSrc > totalDst)
                dst.add(totalSrc - totalDst);
            else if (totalDst > totalSrc)
                src.add(totalDst - totalSrc);
 
            supply = src.stream().mapToInt(i -> i).toArray();
            demand = dst.stream().mapToInt(i -> i).toArray();
 
            costs = new double[supply.length][demand.length];
            matrix = new Shipment[supply.length][demand.length];
 
            for (int i = 0; i < numSources; i++)
                for (int j = 0; j < numDestinations; j++)
                    costs[i][j] = sc.nextDouble();
        }
    }
 
    static void solutionBase(){
        /*AingaMOFIF*/
        System.out.printf("Solution de base \n");
   double totalCosts = 0;
        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) { 
                Shipment s = matrix[r][c];
                if (s != null && s.r == r && s.c == c) {
                    System.out.printf(" %3s ", (int) s.quantity);
                    totalCosts += (s.quantity * s.costPerUnit);
                } else
                    System.out.printf("  -  ");
            }
            System.out.println();
        }
        System.out.printf("Z = %s%n%n", totalCosts);
        /*END MODIF*/
    }
    static void cno() { 
        
        for (int r = 0, northwest = 0; r < supply.length; r++)
            for (int c = northwest; c < demand.length; c++) { 
                int quantity = Math.min(supply[r], demand[c]);
                if (quantity > 0) {
                    matrix[r][c] = new Shipment(quantity, costs[r][c], r, c); 
                    supply[r] -= quantity;
                    demand[c] -= quantity; 
                    if (supply[r] == 0) {
                        northwest = c;
                        break;
                    }
                }/***********************/
                
            }
        solutionBase();        
    }
    static void minili(){
        for (int r = 0; r<supply.length;r++){
            int  currentIndex=demand.length;
            int tab[]=new int[demand.length];
            int indexTab=0;
            tab[0]=1;
            for (int c = 0; c < demand.length; c++) {
                double min = Integer.MAX_VALUE;int index=0;
                for(int i = 0;i<demand.length;i++){
                    boolean trouve=false;
                        //System.out.print(currentIndex+":"+i);
                    for(int x=0;x<indexTab;x++)
                    {
                        if(i==tab[x]){trouve=true;break;}
                    }
                    
                     if(trouve  )continue;   
                     if(min>costs[r][i]) {
                        min=costs[r][i];
                       index=i;
                      
                    }
                        
                     }
                currentIndex=index;
                tab[indexTab]=index;
                indexTab+=1;
                if(demand[index]==0)continue;
                //System.out.println(costs[r][index]);
                    int quantity = Math.min(supply[r],demand[index] );
                    matrix[r][index] = new Shipment(quantity, costs[r][index], r, index); 
                    supply[r] -= quantity;
                    demand[index] -= quantity;
                    if(supply[r] == 0){
                        break;
                    }
                }
        }
        
              solutionBase();  
            }
        
    
    static void minico(){
        for (int r = 0; r<demand.length;r++){
            int  currentIndex=supply.length;
            int tab[]=new int[supply.length];
            int indexTab=0;
            tab[0]=1;
            for (int c = 0; c < supply.length; c++) {
                double min = Integer.MAX_VALUE;int index=0;
                for(int i = 0;i<supply.length;i++){
                    boolean trouve=false;
                        //System.out.print(currentIndex+":"+i);
                    for(int x=0;x<indexTab;x++)
                    {
                        if(i==tab[x]){trouve=true;break;}
                    }
                    
                     if(trouve  )continue;   
                     if(min>costs[i][r]) {
                        min=costs[i][r];
                       index=i;
                      
                    }
                        
                     }
                currentIndex=index;
                tab[indexTab]=index;
                indexTab+=1;
                if(supply[index]==0)continue;
               // System.out.println(costs[index][r]);
                    int quantity = Math.min(demand[r],supply[index] );
                    matrix[index][r] = new Shipment(quantity, costs[index][r], index, r); 
                    demand[r] -= quantity;
                    supply[index] -= quantity;
                    if(demand[r] == 0){
                        break;
                    }
                }
        }
        
              solutionBase();  
            }
        

 
    static void steppingStone() {
        double maxReduction = 0;
        Shipment[] move = null;
        Shipment leaving = null;
 
        fixDegenerateCase();
 
        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) {
 
                if (matrix[r][c] != null)
                    continue;
 
                Shipment trial = new Shipment(0, costs[r][c], r, c);
                Shipment[] path = getClosedPath(trial);
 
                double reduction = 0;
                double lowestQuantity = Integer.MAX_VALUE;
                Shipment leavingCandidate = null;
 
                boolean plus = true;
                for (Shipment s : path) {
                    if (plus) {
                        reduction += s.costPerUnit;
                    } else {
                        reduction -= s.costPerUnit;
                        if (s.quantity < lowestQuantity) {
                            leavingCandidate = s;
                            lowestQuantity = s.quantity;
                        }
                    }
                    plus = !plus;
                }
                if (reduction < maxReduction) {
                    move = path;
                    leaving = leavingCandidate;
                    maxReduction = reduction;
                }
            }
        }
 
        if (move != null) {
            double q = leaving.quantity;
            boolean plus = true;
            for (Shipment s : move) {
                s.quantity += plus ? q : -q;
                matrix[s.r][s.c] = s.quantity == 0 ? null : s;
                plus = !plus;
            }
            steppingStone();
        }
    }
 
    static LinkedList<Shipment> matrixToList() {
        return stream(matrix)
                .flatMap(row -> stream(row))
                .filter(s -> s != null)
                .collect(toCollection(LinkedList::new));
    }
 
    @SuppressWarnings("empty-statement")
    static Shipment[] getClosedPath(Shipment s) {
        LinkedList<Shipment> path = matrixToList();
        path.addFirst(s);
 
        // remove (and keep removing) elements that do not have a
        // vertical AND horizontal neighbor
        while (path.removeIf(e -> {
            Shipment[] nbrs = getNeighbors(e, path);
            return nbrs[0] == null || nbrs[1] == null;
        }));
 
        // place the remaining elements in the correct plus-minus order
        Shipment[] stones = path.toArray(new Shipment[path.size()]);
        Shipment prev = s;
        for (int i = 0; i < stones.length; i++) {
            stones[i] = prev;
            prev = getNeighbors(prev, path)[i % 2];
        }
        return stones;
    }
 
    static Shipment[] getNeighbors(Shipment s, LinkedList<Shipment> lst) {
        Shipment[] nbrs = new Shipment[2];
        for (Shipment o : lst) {
            if (o != s) {
                if (o.r == s.r && nbrs[0] == null)
                    nbrs[0] = o;
                else if (o.c == s.c && nbrs[1] == null)
                    nbrs[1] = o;
                if (nbrs[0] != null && nbrs[1] != null)
                    break;
            }
        }
        return nbrs;
    }
 
    static void fixDegenerateCase() {
        final double eps = Double.MIN_VALUE;
 
        if (supply.length + demand.length - 1 != matrixToList().size()) {
 
            for (int r = 0; r < supply.length; r++)
                for (int c = 0; c < demand.length; c++) {
                    if (matrix[r][c] == null) {
                        Shipment dummy = new Shipment(eps, costs[r][c], r, c);
                        if (getClosedPath(dummy).length == 0) {
                            matrix[r][c] = dummy;
                            return;
                        }
                    }
                }
        }
    }
 
    static void printResult(String filename) {
        System.out.printf("Solution optimale \n");
        double totalCosts = 0; 
        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) { 
                Shipment s = matrix[r][c];
                if (s != null && s.r == r && s.c == c) {
                    System.out.printf(" %3s ", (int) s.quantity);
                    totalCosts += (s.quantity * s.costPerUnit);
                } else
                    System.out.printf("  -  ");
            }
            System.out.println();
        }
        System.out.printf("Z= %s%n%n", totalCosts);
    }
    public static void menu()
    {
            System.out.println("Menu");
            System.out.println("1: MINILI");
            System.out.println("2: MINICO");
            System.out.println("3: Coin Nord Ouest");
            System.out.println("4: Quitter");
            System.out.print("Votre choix : ");
            
    
    }
 
    public static void main(String[] args) throws Exception {
     String file = "C:\\Users\\Ainga\\Documents\\NetBeansProjects\\Transport\\src\\transport\\input.txt";
       String filename = new String(file);
            init(filename);
            int answer = 0;
            System.out.println("*********** TRANSPORT *********");
                        minico();
                        steppingStone();
                        printResult(filename);           
        }
    
}