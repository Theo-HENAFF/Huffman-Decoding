import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Decodage implements Comparator<Lettre> {


	//Create tree out of the list of node with the codeBinaire of the letter(Lettre) associated to the Node
	public ArrayList<Node> createTree(ArrayList<Node> listNode){
		
	    ArrayList<Node> tree = new ArrayList<Node>();
	    int min;
	    int minI1;
	    int minI2;
	    Node minimumNode1;
	    Node minimumNode2;
	    while(listNode.size()>1){
	        min=Integer.MAX_VALUE;
	        minI1=-1;
	        minI2=-1;
	        minimumNode2=null;
	        minimumNode1=null;
	        //Search the letter(lettre) with the smallest frequency
	        for(int i = 0; i<listNode.size();i++){
	            if(listNode.get(i).getLettre().getFrequence()<min){
	                min=listNode.get(i).getLettre().getFrequence();
	                minI1=i;
	            }
	        }
	        
	        
	        //Search the letter(lettre) with the 2nd smallest frequency
	        min=Integer.MAX_VALUE;
	        for(int i = 0; i<listNode.size();i++){
	            if(listNode.get(i).getLettre().getFrequence()<min && i!=minI1){
	                min=listNode.get(i).getLettre().getFrequence();
	                minI2=i;
	            }
	        }
	
	        
	        tree.add(listNode.get(minI1));
	        tree.add(listNode.get(minI2));
	        
	        minimumNode1 = listNode.get(minI1);
	        minimumNode2 = listNode.get(minI2);
	        
	        //Add a node to listeNode composed of a lettre(Class Lettre) with the 2 minimum frequency...
	        int minfreq1 = minimumNode1.getLettre().getFrequence();
	        int minfreq2 = minimumNode2.getLettre().getFrequence();
	        listNode.add(0,new Node(new Lettre(minfreq1+minfreq2,Character.MIN_VALUE),minimumNode1,minimumNode2));
	        
	        //...And remove both node with the smallest frequency
	        listNode.remove(minimumNode1);
	        listNode.remove(minimumNode2);
	
	
	    }
	    tree.add(listNode.get(0));
	    return tree;
	}
	
	//take a starting Node and go trough the tree with the path
	public void parcoursTree(Node node, String path){
	    if(node.getRightChild() == null && node.getLeftChild()==null){
	        node.getLettre().setCodeBinaire(path);
	        System.out.println("label : "+node.getLettre().getLabel()+" path : "+node.getLettre().getCodeBinaire());
	        return;
	    }
	    if(path==null){
	        parcoursTree(node.getRightChild(),"1");
	        parcoursTree(node.getLeftChild(),"0");
	    }else{
	        parcoursTree(node.getRightChild(),path+"1");
	        parcoursTree(node.getLeftChild(),path+"0");
	    }
	
	}



    //Write the decoded text in the file named TextDecode.txt
    public void WriteToFile(String str){
        PrintWriter writer = null;
        try {
            File file = new File("data/TexteDecode.txt");
            writer = new PrintWriter(file, "ISO-8859-1");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(str);
        writer.close();
    }

    //Convert the binary String into readable text
    public String binToChar(String str, ArrayList<Lettre> list_lettre){
        String templist="";
        String newstr="";
        int size = countChar(list_lettre); 
        int tempSize=0;
        for(int i = 0; i < str.length(); i++)//lis la string de bits
        {
            templist+=str.charAt(i);
            for (int j = 0; j < list_lettre.size(); j++) {
                //check if templist is = to an huffman code
                //check for the spare "0" at the end of the string by comparing the actual number of char to the supposed number = size
                if(templist.equals(list_lettre.get(j).getCodeBinaire())&&tempSize<size){
                    str.substring(0,templist.length()-1);//remove the already used huffman code
                    newstr+=list_lettre.get(j).getLabel();
                    templist="";
                    tempSize++;
                }
            }
        }
        return newstr;
    }

    //Count the number of characters in the text
    public int countChar(ArrayList<Lettre>l){
        int size=0;
        for (int i = 0; i < l.size(); i++) {
            size+=l.get(i).getFrequence();
        }
        return size;
    }

    //convert the ascii text into a binary String
    public String byteToBinary(File file) throws IOException{
        byte[] bFile = Files.readAllBytes(file.toPath());
        StringBuilder binary = new StringBuilder();
        String str;
        for (byte b : bFile)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        str = binary.toString();
        return str;
    }

    // Read the frequence file to create a list composed of lettre
    public ArrayList<Lettre> creationLettre(File frequencyFile){
        ArrayList<Lettre> list_lettre = new ArrayList<Lettre>();
        if (!frequencyFile.exists()) {
            System.out.println("frequencyFile" + " does not exist.");
            return null;
        }
        if (!(frequencyFile.isFile() && frequencyFile.canRead())) {
            System.out.println(frequencyFile.getName() + " cannot be read from.");
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(frequencyFile);
            char current;
            char before='~';
            String str;
            boolean reg;
            boolean reg1;
            boolean reg2;
            boolean token= true;
            boolean token1 = true;
            boolean token2 = false;
            boolean declancheur = false;
            char templabel = '~';
            String freq ="";
            
            while (fis.available() > 0) {
                current = (char) fis.read();
                
                str = String.valueOf(current);
                reg = Pattern.matches("( )", str);//True or false if space
                reg1 = Pattern.matches("\\n", String.valueOf(before));
                reg2 = Pattern.matches("\\r", String.valueOf(before));
                if (declancheur){//attend la premier itération du while
                    if(token2){//lis char by char la freq d'un character
                        if(Pattern.matches("[0-9]+", String.valueOf(current))){
                            freq+=current;//construit la string de freq
                        }else{

                            list_lettre.add(new Lettre(Integer.parseInt(freq),templabel));
                            token2=false;
                            freq="";
                        }
                    }
                    if(!reg1 && !reg2 && reg && !token2){//si l'on est au niveau d'un espace entre un char et une fréquence
                        templabel=before;
                        token2=true;
                    }
                    if(token){//Si le character \n n'a pas encore été lu
                        if(reg && reg1 && !token2) {
                            templabel=before;
                            token2 = true;
                            token = false;
                        }
                    }
                    if(token1){//Si le character \r n'a pas encore été lu
                        if(reg && reg2 && !token2) {
                            templabel=before;
                            token2 = true;
                            token1=false;
                        }
                    }
                }
                before = current;
                declancheur = true;
            }
            fis.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list_lettre;
    }
    
    
    //Create a list of Node from the list of lettre
	public ArrayList<Node> initiateNodes (ArrayList<Lettre> listLettre){
	    ArrayList<Node> nodeList = new ArrayList<Node>();
	    
	    for(int i=0; i<listLettre.size();i++){
	        nodeList.add(new Node(listLettre.get(i),null,null));
	    }
	    return nodeList;
	}
	
	//Sort every node from the Node list, sorted by frequence
	public void sortNodes(ArrayList<Node> listeNode){
	    listeNode.sort(Comparator.comparing((Node n1) -> n1.getLettre().getFrequence()).thenComparing(n1 -> n1.getLettre().getLabel()));
	}
	
	//the main method to decode the text, it's a synthesis of all the previous method
	public void decodeFile()throws IOException{
	    Scanner myObj = new Scanner(System.in);
	    String compressedFileName;
	    String frequencyFileName;
	    System.out.println("Enter compressedFileName"); 
	    compressedFileName= myObj.nextLine();
	    System.out.println("Enter frequencyFileName"); 
	    frequencyFileName= myObj.nextLine();
	    
		File compressedFile = new File("data/" + compressedFileName);
		File frequencyFile = new File("data/" + frequencyFileName);
	
        ArrayList<Lettre> listLettre = new ArrayList<Lettre>();
        ArrayList<Node> listNode = new ArrayList<Node>();
        ArrayList<Node> tree = new ArrayList<Node>();
        
        listLettre=creationLettre(frequencyFile);
        listNode = initiateNodes(listLettre);
        sortNodes(listNode);
        
        tree = createTree(listNode);
        parcoursTree(tree.get(tree.size()-1),null);
        
        String bits = byteToBinary(compressedFile);
        String unZipText=binToChar(bits,listLettre);
        System.out.println("unzip : "+unZipText);
        WriteToFile(unZipText);
        
        myObj.close();
    }

    @Override
    public int compare(Lettre o1, Lettre o2) {
        return 0;
    }
}

