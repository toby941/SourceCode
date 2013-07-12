package com.sourcecode.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DFSOperator {
    private static final String ROOT_PATH = "hdfs:///";
    private static final int BUFFER_SIZE = 4096;

    /** * construct. */
    public DFSOperator() {
    }

    /**
     * Create a file on hdfs.The root path is /.<br>
     * for example: DFSOperator.createFile("/lory/test1.txt", true);
     * 
     * @param path the file name to open
     * @param overwrite if a file with this name already exists, then if true, the file will be
     * @return true if delete is successful else IOException.
     * @throws IOException
     */
    public static boolean createFile(String path, boolean overwrite) throws IOException {
        // String uri = "hdfs://192.168.1.100:9000";
        // FileSystem fs1 = FileSystem.get(URI.create(uri), conf);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path f = new Path(ROOT_PATH + path);
        fs.create(f, overwrite);
        fs.close();
        return true;
    }

    /**
     * * Delete a file on hdfs.The root path is /. <br>
     * * for example: DFSOperator.deleteFile("/user/hadoop/output", true); * @param path the path to delete * @param
     * recursive if path is a directory and set to true, the directory is deleted else throws an exception. In case of a
     * file the recursive can be set to either true or false. * @return true if delete is successful else IOException. * @throws
     * IOException
     */
    public static boolean deleteFile(String path, boolean recursive) throws IOException {
        // String uri = "hdfs://192.168.1.100:9000";
        // FileSystem fs1 = FileSystem.get(URI.create(uri), conf);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path f = new Path(ROOT_PATH + path);
        fs.delete(f, recursive);
        fs.close();
        return true;
    }

    /**
     * * Read a file to string on hadoop hdfs. From stream to string. <br>
     * * for example: System.out.println(DFSOperator.readDFSFileToString("/user/hadoop/input/test3.txt")); * @param path
     * the path to read * @return true if read is successful else IOException. * @throws IOException
     */
    public static String readDFSFileToString(String path) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path f = new Path(ROOT_PATH + path);
        InputStream in = null;
        String str = null;
        StringBuilder sb = new StringBuilder(BUFFER_SIZE);
        if (fs.exists(f)) {
            in = fs.open(f);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            while ((str = bf.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            in.close();
            bf.close();
            fs.close();
            return sb.toString();
        }
        else {
            return null;
        }
    }

    /**
     * * Write string to a hadoop hdfs file. <br>
     * * for example: DFSOperator.writeStringToDFSFile("/lory/test1.txt", "You are a bad man.\nReally!\n"); * @param
     * path the file where the string to write in. * @param string the context to write in a file. * @return true if
     * write is successful else IOException. * @throws IOException
     */
    public static boolean writeStringToDFSFile(String path, String string) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        FSDataOutputStream os = null;
        Path f = new Path(ROOT_PATH + path);
        os = fs.create(f, true);
        os.writeBytes(string);
        os.close();
        fs.close();
        return true;
    }

    public static void listAll(String dir) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        FileStatus[] stats = fs.listStatus(new Path(dir));

        for (int i = 0; i < stats.length; ++i) {
            if (!stats[i].isDir()) {
                // regular file
                System.out.println(stats[i].getPath().toString());
            }
            else if (stats[i].isDir()) {
                // dir
                System.out.println(stats[i].getPath().toString());
            }

        }
        fs.close();
    }

    public static void main(String[] args) {
        try {
            listAll(ROOT_PATH);
            // Configuration conf = new Configuration();
            // FileSystem fs = FileSystem.get(conf);
            // Path f = new Path(ROOT_PATH);
            //
            // System.out.println(f.toString());
            // DFSOperator.createFile("/lory/test1.txt", true);
            // DFSOperator.deleteFile("/dfs_operator.txt", true);
            // DFSOperator.writeStringToDFSFile("/lory/test1.txt", "You  are  a  bad  man.\nReally?\n");
            // System.out.println(DFSOperator.readDFSFileToString("/lory/test1.txt"));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("===end===");
    }
}
