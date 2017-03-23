/**
 * Created by fadzly on 3/21/17.
 */
package awesome.fadzly;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLReader {
    private File dataFile;
    private String dir;
    private String configurationName;
    private int maxDepth;

    private NodeList nList = null;

    public XMLReader(String directory, String configuration, int depth) {
        dir = directory;
        configurationName = configuration;
        maxDepth = depth;
        traverseDir();
    }

    /**
     * Traverse thru dir given to get data file
     */
    private void traverseDir(){
        String fileNames = "";
        Path start = Paths.get(dir);
        try (Stream<Path> stream = Files.find(start, maxDepth, (path, attr) ->
                String.valueOf(path).endsWith("data"))) {
            fileNames = stream
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining("; "));
        }catch (IOException r){
            System.out.println("Directory invalid : " + r.getMessage());
        }
        String[] filesArray = fileNames.split("; ");
        for(String file:filesArray){
            dataFile = new File(file);
            processFile();
        }
    }

    /**
     * Process data file(xml) to check whether config(input) is being used by other config
     * Config(Input) : ec,profile,ultra...
     */
    public void processFile(){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(dataFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            nList = doc.getElementsByTagName("dr.StuffyMapperData.sourceCode:Source_code");
            getAPLAttributes();
            nList = doc.getElementsByTagName("dr.DRConfiguration:Value");
            getConfigurationAttributes();
            nList = doc.getElementsByTagName("dr.DRConfiguration:External_Reference_Profile");
            getConfigurationAttributes();
            nList = doc.getElementsByTagName("dr.UltraClientInfo:value");
            getUltraAttributes();
            nList = doc.getElementsByTagName("dr.PicoInstanceEntity:value");
            getPicoInstanceAttribute();
            nList = doc.getElementsByTagName("dr.dbprofileData:dr.dbprofileData");
            getDBProfileAttributes();
        } catch (Exception e) {
            System.out.println("Unable to read data file : " + e.getMessage());
        }
    }

    /**
     * Checking if ec/ecsa is being used.
     * @return boolean (true if found)
     */
    private boolean getPicoInstanceAttribute(){
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if(nNode.hasChildNodes()) {
                NodeList childNList = nNode.getChildNodes();
                for(int i = 0; i < childNList.getLength(); i++) {
                    Node childNode = childNList.item(i);
                    if(childNode.hasAttributes()) {
                        NamedNodeMap map = childNode.getAttributes();
                        for (int k = 0; k < map.getLength(); k++) {
                            if(map.item(k).getNodeValue().equals(configurationName)){
                                print(dataFile.getParent());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checking if ultra config is being used.
     * @return boolean (true if found)
     */
    private boolean getUltraAttributes(){
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if(nNode.hasAttributes()
                    && nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                String formatName = eElement.getAttribute("FormatName");
                if(formatName.substring(formatName.indexOf(".") + 1).equals(configurationName)){
                    print(dataFile.getParent());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checking if DB Profile is being used.
     * @return boolean (true if found)
     */
    private boolean getDBProfileAttributes(){
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if(nNode.hasChildNodes()) {
                NodeList childNList = nNode.getChildNodes();
                for(int i = 0; i < childNList.getLength(); i++) {
                    Node childNode = childNList.item(i);
                    if(childNode.hasAttributes()) {
                        NamedNodeMap map = childNode.getAttributes();
                        for (int k = 0; k < map.getLength(); k++) {
                            String value = map.item(k).getNodeValue();
                            if(value.contains(".") && value.substring(value.indexOf(".") + 1).equals(configurationName)){
                                print(dataFile.getParent());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checking if config is being used.
     * @return boolean (true if found)
     */
    private boolean getConfigurationAttributes(){
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if(nNode.hasAttributes()
                    && nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                if(eElement.getAttribute("Name").equals(configurationName)){
                    print(dataFile.getParent());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checking if config is being used in stuffymapper/APL.
     * @return boolean (true if found)
     */
    private boolean getAPLAttributes(){
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if(nNode.getNodeType() == Node.ELEMENT_NODE){
                Element eElement = (Element) nNode;
                if(eElement.getElementsByTagName("Value").item(0).getTextContent().contains(configurationName)){
                    printAPL(dataFile.getParent());
                    return true;
                }
            }
        }
        return false;
    }

    public void print(String name){
        File file = new File(name);
        System.out.println("Being used by : " + file.getName());
    }

    public void printAPL(String name){
        File file = new File(name);
        System.out.println("Might being used by : " + file.getName() + " (APL)");
    }

}
