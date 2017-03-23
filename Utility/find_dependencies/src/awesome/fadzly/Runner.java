/* Copyright 2016 Digital Route AB. All rights reserved.
 * Proprietary and Confidential
*/

/**
 * Created by fadzly on 3/21/17.
 */
package awesome.fadzly;

public class Runner {

    public static void main(String[] args){
        int depth = 5;
        if(args.length < 2){
            System.out.println("Invalid Command");
            System.out.println("Command e.g: java -jar find_dependecies.jar <dir> <config_name> <depth>");
            System.exit(1);
        }
        if(args.length >= 3){
            depth = Integer.parseInt(args[2]);
        }
        String dir = args[0];
//        String dir = "/home/fadzly/Work/Project/3rdClone/mz-main/mediationzone/mzhomes/home1/data/space/active/content/config/";
        String configuration = args[1];
        //"PostShift_Internal_Ultra"; //"EC_01_TecNextBatch";//"PostShift_IWF_02";//"PostShift_Internal_Ultra";

        XMLReader reader = new XMLReader(dir, configuration, depth);
    }
}
