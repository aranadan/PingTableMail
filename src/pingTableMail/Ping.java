package pingTableMail;

import pingTableMail.controllers.MainController;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public  class  Ping extends Thread{

    private Process p = null;
    private final IpHost host;


    public Ping (IpHost host){
        this.host = host;
        System.out.println(getId() + " - Ping Thread start");
    }

    @Override
    public void run() {
        do {

            try {
                p = Runtime.getRuntime().exec("ping -n 3 " + host.getIp());

            } catch (IOException e) {
                e.printStackTrace();
            }

            try (Scanner in = new Scanner(p.getInputStream(), "cp866")) {
                String pingLine;
                int ttlCount = 0;
                for (int j = 0; j < 5; j++) {
                    pingLine = in.nextLine();
                    if (pingLine.contains("TTL=")) {

                        ttlCount++;
                        if (j == 4) {

                            String values[] = pingLine.split("[=<]");
                            host.setTime(MainController.dateFormat.format(new Date()));
                            host.setResponse(values[2].replaceAll("TTL", ""));

                            if (host.getStatus().equals("DOWN")) {
                                host.setStatus("UP");
                                System.out.println( getId() + " : " + host.getHostName() + " : "+ host.getStatus() + " : "+ host.getTime());
                            }
                        }
                    }

                    if (j == 4 & !pingLine.contains("TTL=") & host.getStatus().equals("UP")) {

                        host.setStatus("DOWN");
                        System.out.println( getId() + " : " + host.getHostName() + " : "+ host.getStatus() + " : "+ host.getTime());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }while (MainController.ISACTIVETHREAD);

    }

}