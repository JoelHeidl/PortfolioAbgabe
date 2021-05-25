package com.education.mosbach.utils;

import java.util.Random;

/**
 * @author Joel Heidl
 */
    public class DummyPortfolio {

        static class Marktplatz implements Runnable {

            private static int[] rohstoffLager = new int[5];
            private static int[] endproduktLager = new int[6];
            private static int[] gekaufteEndProdukte = new int[6];
            private static int[] gekaufteRohstoffeProZeiteinheit = new int[5];

            private static Marktplatz marktplatz = new Marktplatz();

            private Marktplatz() {}

            public static Marktplatz getMarktplatz() {
                return marktplatz;
            }

            /**
             * @param A wird vom Lieferanten geliefert und in Rohstofflager eingelagert
             */
            static void einlagernRohL(int[] A) {
                synchronized (rohstoffLager) {
                    for (int i = 0; i < rohstoffLager.length; i++) {
                        rohstoffLager[i] += A[i];
                    }
                }
            }

            /**
             * @param endProdukt wird vom Lieferanten produziert und in Endproduktlager eingelagert
             */
            static void einlagernEndL(int endProdukt) {
                synchronized(endproduktLager){
                    endproduktLager[endProdukt] = endproduktLager[endProdukt] + 1;
                }
            }

            /**
             * wird vom Konsumenten aufgerufen und entnimmt Endprodukte aus dem Endproduktlager
             */
            static void kaufen() {
                Random random = new Random();
                int käufe = random.nextInt(2) + 3;
                for (int i = 0; i < käufe; i++) {
                    int endProdukt = random.nextInt(6);

                    synchronized (endproduktLager) {
                        if (endproduktLager[endProdukt] > 0){
                            endproduktLager[endProdukt] = endproduktLager[endProdukt] - 1;
                            gekaufteEndProdukte[endProdukt] = gekaufteEndProdukte[endProdukt] + 1;
                        }
                    }
                }
            }

            void ausgebeneGekaufteRohstoffeProZeiteinheit () {
                System.out.println();
                System.out.println("Gekaufte Rohstoffe pro Zeiteinheit: ");
                for (int i = 0; i < gekaufteRohstoffeProZeiteinheit.length; i++) {
                    System.out.println("Rohstoff " + i + ": " + gekaufteRohstoffeProZeiteinheit[i] + " mal gekauft");
                }
                for (int i = 0; i < gekaufteRohstoffeProZeiteinheit.length; i++) {

                    gekaufteRohstoffeProZeiteinheit[i] = 0;
                }
            }

            void ausgebenGekaufteEndProdukte(){
                System.out.println();
                System.out.println("Insgesamt gekaufte Endprodukte: ");
                for (int i = 0; i < gekaufteEndProdukte.length; i++) {
                    System.out.println("Endprodukt " + i + ": " + gekaufteEndProdukte[i] + " mal gekauft");
                }
            }

            /**
             * wird vom Produzent aufgerufen, entnimmt Rohstoffe aus dem Rohstofflager zur Produktion
             *
             * @param rohstoff ist die Indexnummer im Rohstofflager
             * @param verbrauch ist der Verbrauch des angesprochenen Rohstoffes
             */
            static void auslagernRohL(int rohstoff, int verbrauch){
                synchronized(rohstoffLager){

                    gekaufteRohstoffeProZeiteinheit[rohstoff] += verbrauch;
                    rohstoffLager[rohstoff] -= verbrauch;

                }

            }

            void ausgebenrohstoffL() {
                System.out.println("Rohstoffe auf Lager: ");
                for(int i = 0; i< rohstoffLager.length; i++) {
                    System.out.println("Rohstoff " + i + " hat " + rohstoffLager[i] + " Elemente");
                }
            }

            void ausgebenEndL(){
                System.out.println();
                System.out.println("Endprodukte auf Lager: ");
                for (int i = 0; i < endproduktLager.length; i++) {
                    System.out.println("Endprodukt " + i + " hat " + endproduktLager[i] + " Elemente");
                }
            }

            @Override
            public void run() {
                ausgebenrohstoffL();
                ausgebeneGekaufteRohstoffeProZeiteinheit();
                ausgebenEndL();
                ausgebenGekaufteEndProdukte();
            }
        }

    static class RohstoffLieferant implements Runnable{

            /**
             * erzeugt ein Rohstoffarray mit 5 Rohstoffen
             *
             * entweder werden zwischen 5 und 10 Mengeneinheiten pro Rohstoff dann eingelagert oder nichts
             */
            @Override
            public void run() {

                int[] rohstoffe = new int[5];
                Random random = new Random();
                for (int i = 0; i < rohstoffe.length; i++) {
                    rohstoffe[i] = random.nextInt(10);
                    if(rohstoffe[i] < 5){
                        rohstoffe[i] = 0;
                    }
                }
                Marktplatz.einlagernRohL(rohstoffe);
            }
        }

        static class Produzent implements Runnable{

            int ID;

            public Produzent(int ID){

                this.ID = ID;
            }

            /**
             * jeder Produzent hat eigene ID
             * --> ID bestimmt Produktionsweg durch switch-case
             *
             * wenn genug Rohstoffe für einen Produktionsweg vorhanden sind, werden die Rohstoffe ausgelagert
             * und die Endprodukte ins Endproduktlager eingelagert
             *
             * der Produzent kann viermal hintereinander seine beiden Produktionen durchlaufen
             * --> somit kann ein Produzent pro Zeiteinheit max. 8 Produkte erstellen
             */
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    switch (ID) {

                        case 1:
                            if (Marktplatz.rohstoffLager[0] >= 3 && Marktplatz.rohstoffLager[1] >= 1 && Marktplatz.rohstoffLager[2] >= 1) {
                                Marktplatz.einlagernEndL(0);
                                Marktplatz.auslagernRohL(0, 3);
                                Marktplatz.auslagernRohL(1, 1);
                                Marktplatz.auslagernRohL(2, 1);
                            }
                            if (Marktplatz.rohstoffLager[2] >= 2 && Marktplatz.rohstoffLager[3] >= 2 && Marktplatz.rohstoffLager[4] >= 2) {
                                Marktplatz.einlagernEndL(1);
                                Marktplatz.auslagernRohL(2, 2);
                                Marktplatz.auslagernRohL(3, 2);
                                Marktplatz.auslagernRohL(4, 2);
                            }
                            break;

                        case 2:
                            if (Marktplatz.rohstoffLager[0] >= 4 && Marktplatz.rohstoffLager[2] >= 2 && Marktplatz.rohstoffLager[4] >= 2) {
                                Marktplatz.einlagernEndL(2);
                                Marktplatz.auslagernRohL(0, 4);
                                Marktplatz.auslagernRohL(2, 2);
                                Marktplatz.auslagernRohL(4, 2);
                            }
                            if (Marktplatz.rohstoffLager[1] >= 1 && Marktplatz.rohstoffLager[2] >= 1 && Marktplatz.rohstoffLager[3] >= 1) {
                                Marktplatz.einlagernEndL(3);
                                Marktplatz.auslagernRohL(1, 1);
                                Marktplatz.auslagernRohL(2, 1);
                                Marktplatz.auslagernRohL(3, 1);
                            }
                            break;

                        case 3:
                            if (Marktplatz.rohstoffLager[1] >= 1 && Marktplatz.rohstoffLager[3] >= 2) {
                                Marktplatz.einlagernEndL(4);
                                Marktplatz.auslagernRohL(1, 1);
                                Marktplatz.auslagernRohL(3, 2);
                            }
                            if (Marktplatz.rohstoffLager[0] >= 1 && Marktplatz.rohstoffLager[4] >= 2) {
                                Marktplatz.einlagernEndL(5);
                                Marktplatz.auslagernRohL(0, 1);
                                Marktplatz.auslagernRohL(4, 2);
                            }

                            break;
                    }
                }
            }
        }

        static class Konsument implements Runnable{

            @Override
            public void run() {
                Marktplatz.kaufen();
            }
        }

        public static void main(String[] args) throws InterruptedException {

            Marktplatz marktplatz = Marktplatz.getMarktplatz();

            for (int i = 1; i <=20;i++) {

                Thread rohstoffLieferant1 = new Thread(new RohstoffLieferant());
                Thread rohstoffLieferant2 = new Thread(new RohstoffLieferant());
                Thread rohstoffLieferant3 = new Thread(new RohstoffLieferant());

                Thread produzent1 = new Thread(new Produzent(1));
                Thread produzent2 = new Thread(new Produzent(2));
                Thread produzent3 = new Thread(new Produzent(3));

                Thread konsument1 = new Thread(new Konsument());
                Thread konsument2 = new Thread(new Konsument());
                Thread konsument3 = new Thread(new Konsument());
                Thread konsument4 = new Thread(new Konsument());
                Thread konsument5 = new Thread(new Konsument());

                Thread marktplatzT = new Thread(marktplatz);

                rohstoffLieferant1.start();
                rohstoffLieferant2.start();
                rohstoffLieferant3.start();
                rohstoffLieferant1.join();
                rohstoffLieferant2.join();
                rohstoffLieferant3.join();

                produzent1.start();
                produzent1.join();
                produzent2.start();
                produzent2.join();
                produzent3.start();
                produzent3.join();

                konsument1.start();
                konsument2.start();
                konsument3.start();
                konsument4.start();
                konsument5.start();
                konsument1.join();
                konsument2.join();
                konsument3.join();
                konsument4.join();
                konsument5.join();

                System.out.println();
                System.out.println("-----------------------------------------------------------------");
                System.out.println();
                System.out.println(i + ". Durchlauf:");

                marktplatzT.start();
                marktplatzT.join();

            }
        }
    }