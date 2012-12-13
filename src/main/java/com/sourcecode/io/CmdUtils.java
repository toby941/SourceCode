package com.sourcecode.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmdUtils {
    public static void main(String[] args) {

        Process p = null;
        InputStream is = null;
        BufferedReader br = null;
        boolean antBuildSucc = false;
        String execShell = "ant -f D:\\workspace\\passTool\\build.xml";
        // String[] cmds = new String[]{"/bin/bash", "-c", execShell};
        String[] cmds = new String[]{"cmd ", "/c", execShell};

        try {
            System.out.println("begin exec...");
            p = Runtime.getRuntime().exec(cmds);
            // p = Runtime.getRuntime().exec("cmd /c ant -f D:\\workspace\\passTool\\build.xml");
            is = p.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            // 等待打包完毕
            System.out.println("wait for exec...");
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
                if (line.indexOf("BUILD SUCCESSFUL") != -1) {
                    antBuildSucc = true;
                    System.out.println("apk: package success");
                }
            }
            if (!antBuildSucc) {
                System.err.println("ant: console packing error!  " + sb.toString());
            }

            br.close();
            is.close();
            p.waitFor();
            p.destroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
