package org.seleniumhq.selenium;

    import javafx.scene.input.KeyCode;
    import yahoofinance.histquotes.HistoricalQuote;

    import java.awt.*;
    import java.awt.event.KeyEvent;
    import java.awt.event.KeyListener;
    import java.awt.font.FontRenderContext;
    import java.awt.font.LineMetrics;
    import java.awt.geom.*;
    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileReader;
    import java.io.IOException;
    import java.text.DateFormat;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import javax.swing.*;

    public class GraphingData extends JPanel implements KeyListener {

        ArrayList<String> date = new ArrayList<String>();
        ArrayList<Integer> hist_data = new ArrayList<Integer>();
        ArrayList<String> ticker = new ArrayList<String>();
        ArrayList<Integer> data = new ArrayList<Integer>();

    //  int[] data = {
    //          21, 14, 18, 03, 86, 88, 74, 87, 54, 77,
    //          61, 55, 48, 60, 49, 36, 38, 27, 20, 18
    //  };

        final int PAD = 10;
        int count=0;
        int tickerindex=0;
        int arraylength;
        public GraphingData(){
        }

        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyCode();
            switch (keycode){
                case KeyEvent.VK_RIGHT:
                    if(tickerindex>=arraylength){

                        tickerindex=0;
                    }
                    else {
                        tickerindex++;
                    }
                    data.clear();
                    hist_data.clear();
                    date.clear();
                    count=0;
                    break;
                case KeyEvent.VK_LEFT:
                    if(tickerindex<0){
                        tickerindex = arraylength;

                    }
                    else{
                        tickerindex--;
                    }
                    data.clear();
                    hist_data.clear();
                    date.clear();
                    count=0;
                    break;
            }


        }

        public void keyReleased(KeyEvent e) {

        }

        public void keyTyped(KeyEvent e) {

        }

        protected void paintComponent(Graphics g)  {

        //  if(count == 100){
        //      data.clear();
        //      hist_data.clear();
        //      date.clear();
        //      tickerindex++;
        //      count=0;
        //  }
            if(count ==0) {
                try {
                    readFile();

                } catch (IOException e) {
                    System.out.println("error: " + e.getMessage());
                }
                data.addAll(hist_data);
            }
            System.out.println(count);
            count++;
            super.repaint();
            super.paintComponent(g);
            this.setBackground(Color.black);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight()-140;
            // Draw ordinate.
            g2.draw(new Line2D.Double(PAD, PAD, PAD, h - PAD));
            // Draw abcissa.
            g2.draw(new Line2D.Double(PAD, h - PAD, w - PAD, h - PAD));
            // Draw labels.
            Font font = g2.getFont();
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = font.getLineMetrics("0", frc);
            float sh = lm.getAscent() + lm.getDescent();
            // Ordinate label.
            String s = "data";
            float sy = PAD + ((h - 2 * PAD) - s.length() * sh) / 2 + lm.getAscent();
            for (int i = 0; i < s.length(); i++) {
                String letter = String.valueOf(s.charAt(i));
                float sw = (float) font.getStringBounds(letter, frc).getWidth();
                float sx = (PAD - sw) / 2;
                g2.drawString(letter, sx, sy);
                sy += sh;
            }
            // Abcissa label.
            s = "x axis";
            sy = h - PAD + (PAD - sh) / 2 + lm.getAscent();
            float sw = (float) font.getStringBounds(s, frc).getWidth();
            float sx = (w - sw) / 2;
            g2.drawString(s, sx, sy);
            // Draw lines.
            double xInc = (double) (w - 2 * PAD) / (data.size() );
            double scale = (double) (h - 2 * PAD) / getMax();
            g2.setPaint(Color.green.darker());

            g2.drawString(ticker.get(tickerindex), sx+150, sy-100);
            System.out.println(tickerindex);
         // for (int i = 0; i < data.size() - 1; i++) {
         //     double x1 = PAD + i * xInc;
         //     double y1 = h - PAD - scale * data.get(i);
         //     double x2 = PAD + (i + 1) * xInc;
         //     double y2 = h - PAD - scale * (data.get(i)+1);
         //     g2.draw(new Line2D.Double(x1, y1, x2, y2));
         // }
            // Mark data points.
            g2.setPaint(Color.red);
            for (int i = 0; i < data.size(); i++) {
                double x = PAD + i * xInc;
                double y = h - PAD - scale * data.get(i);
                g2.fill(new Ellipse2D.Double(x - 2, y - 2, 3,3));

                if(i%85==0 ) {
                    g2.drawString(date.get(i), (int) x - 2, super.getHeight() -120);
                }
            }
        }

        private int getMax() {
            int max = -Integer.MAX_VALUE;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) > max)
                    max = data.get(i);
            }
            return max;
        }

//Reads csv Data for stock file 2017-11-14_Stocks.csv
//Puts data into data structure for plotting
        private void readFile() throws IOException {
            System.out.println("reading file");
            int data;
            String csvFile = "2017-11-14_Stocks.csv";
            FileReader file = new FileReader(csvFile);
            String line = "";
            String cvsSplitBy = ",";
            BufferedReader br = new BufferedReader(file);
            int index =0;
            try  {

                while ((line = br.readLine()) != null) {

                    // use comma as separator
                    String[] csvData = line.split(cvsSplitBy);

                    if(index==0){
                        for(int i=2;i<csvData.length;i++)
                        date.add(csvData[i]);
                    }
                    if(index > 0 ) {

                        ticker.add(csvData[0]);
                        arraylength = ticker.size();

                        //Ticker information read here
                        if(tickerindex<0) tickerindex=ticker.size()-1;
                        if(csvData[0].compareTo(ticker.get(tickerindex))==0) {
                            try {
                                for (int i = 3; i < csvData.length; i++) {
                                    data = (int) Double.parseDouble(csvData[i]);
                                    hist_data.add(data);
                                }
                            } catch (NumberFormatException e) {
                                continue;
                            }
                        }
                    }

                    index++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }