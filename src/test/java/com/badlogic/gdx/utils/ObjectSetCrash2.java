package com.badlogic.gdx.utils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

public class ObjectSetCrash2 extends ApplicationAdapter {
    private static final String[] problemStrings = (
               "21oo 0oq1 0opP 0ooo 0pPo 21pP 21q1 1Poo 1Pq1 1PpP 0q31 0pR1 0q2P 0q1o 232P 231o 2331 0pQP" 
            + " 22QP 22Po 22R1 1QQP 1R1o 1QR1 1R2P 1R31 1QPo 1Qup 1S7p 0r8Q 0r7p 0r92 23X2 2492 248Q 247p" 
            + " 22vQ 22up 1S92 1S8Q 23WQ 23Vp 22w2 1QvQ 1Qw2 1RVp 1RWQ" // normally crashes here 
                   + " 1RX2 0qX2" //  crashes here even with -Xmx8G 
                   + " 0qWQ 0qVp 0pvQ 0pup 0pw2" // it may get part-way through these with -Xmx16G, which is _huge_
        
//               "l1nqp kPoRp jpOqp jpOs2 jop3p l2Os2 l2Q4Q l2Q3p l2PSQ kR0rQ l1p3p l1nrQ l2OrQ kQPRp" 
//            + " d7PoU d7R1U d6ooU d6q26 d8326 cVR1U d81oU buR26 bv1oU cUq26 cUop6 d7QOt cW30t" 
//            + " rTW1S rU7Or qrv0r rU7Q4 qsW1S qsUnr qrv1S qsVOr qt7Q4 qrtoS s680r s5UoS s66nr" 
//            + " 8PUQu 8PUS7 7ntQu 7oUQu 8Osr7 7oV3V 7nsr7 7nsqV 8Q5r7 90tQu 90u2u 7nu47 7ntS7"
//            + " HR2Y8 Gq39W HPox8 I1pY8 I1pWv HR2Wv HPq9W I31x8 I2R9W I339W I1ovv HPpXW"
//            + " r53nu qST27 r54Ou r53p7 qT4Q7 pqrQ7 r551V r550u qT50u qT3p7 qT3oV qRs0u"
//            + " ER321 EPq21 EPooP ER1no F2Pno F1op1 Dq321 DpR0o EQPp1 F32Q1 F32PP F31oP"
//            + " boTtR bntTq bnsu3 bnu6R cQ6V3 cQ76R cOstR cPTu3 boUV3 d1TtR d1V73 d0tV3"
//            + " JvS20 KWQp0 L92oO Jur1O KVqQ0 Jw2nn L940n KWRPO JuqQ0 Jupnn JvS0n JvQp0"
//            + " OX5pT OVtPs Nw735 OVtR5 OX5os Nw72T Nuu35 OVspT P7spT OX6Ps P7u2T P8TpT"
//            + " TQ8vw U1XY9 Sp9Y9 TOx8w SoWwX Snx8w U1XXX U1XWw U1WwX SoY9X U1Y8w U29Y9"
//            + " ETtR- EV5ok DtTok Dsu1k Dsu3- ETu3- EUV2L F6UPk Du73- DtUR- Du5q- Du71k"
//            + " JLu64 JN6Sr Im74r Im75S JN5sS JMUU4 K-ssS IlV5S Im6TS Im5sS Iku4r"
//            + " PTvRS Osupr OsvQr Q77pr Q6WS4 PV7pr Ou8RS Ou7qS OsvS4 OtVpr PTw44"
//            + " RXP90 RWnWO RY17n Qx0Vn S8nX0 S9OX0 RXNvO RY0X0 RY0WO RY0Vn S8mvO"
//            + " X1v65 Vq7TT X2VSs X2Ut5 X36rs WR7U5 Vq85T Vq6rs X1uTT VouSs WPuU5"
//            + " ko498 knS98 lNr98 lOQvW ko3X8 knQuv knQw8 lNpuv lNpvW kmpvW ko3Vv"
//            + " q3t4r pRt5S os4sS q4U5S q3t64 oqrt4 q3sU4 q54rr q55U4 pRt64 pRrrr"
//            + " xRPo8 y3PnW xQonW xRPnW xQq18 xRR18 xS30W wr2P8 wqQNv xQomv y42Nv"
//            + " jRuw7 k3vVu irX7u iqvWV iqvVu k4Vw7 k4X8V irVuu iqvX7 k3uuu jSVuu"
//            + " 2fV81 2fUVP 3FtUo 2fV6o 3GUW1 2esv1 3H781 3GV7P 2fUW1 2g781 3FsuP"
//            + " 3T6S1 44US1 456S1 44V41 2s6S1 2s5r1 2rUQo 3Rspo 44URP 3SURP 43spo"
    ).split(" ");
//mySet.addAll("l1nqp kPoRp jpOqp jpOs2 jop3p l2Os2 l2Q4Q l2Q3p l2PSQ kR0rQ l1p3p l1nrQ l2OrQ kQPRp d7PoU d7R1U d6ooU d6q26 d8326 cVR1U d81oU buR26 bv1oU cUq26 cUop6 d7QOt cW30t rTW1S rU7Or qrv0r rU7Q4 qsW1S qsUnr qrv1S qsVOr qt7Q4 qrtoS s680r s5UoS s66nr 8PUQu 8PUS7 7ntQu 7oUQu 8Osr7 7oV3V 7nsr7 7nsqV 8Q5r7 90tQu 90u2u 7nu47 3, 2, 1... 7ntS7".split(" "));
//mySet.addAll("21oo 0oq1 0opP 0ooo 0pPo 21pP 21q1 1Poo 1Pq1 1PpP 0q31 0pR1 0q2P 0q1o 232P 231o 2331 0pQP 22QP 22Po 22R1 1QQP 1R1o 1QR1 1R2P 1R31 1QPo 1Qup 1S7p 0r8Q 0r7p 0r92 23X2 2492 248Q 247p 22vQ 22up 1S92 1S8Q 23WQ 23Vp 22w2 1QvQ 1Qw2 1RVp 1RWQ 1RX2 0qX2".split(" "));
    private ObjectSet<String> theSet;
    @Override
    public void create() {
        theSet = new ObjectSet<>(problemStrings.length);
//        String book = "";
//        try {
//            book = new String(Files.readAllBytes(Paths.get("res/bible_only_words.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        theSet.addAll(book.split(" "));
//        theSet.addAll("l1nqp kPoRp jpOqp jpOs2 jop3p l2Os2 l2Q4Q l2Q3p l2PSQ kR0rQ l1p3p l1nrQ l2OrQ kQPRp d7PoU d7R1U d6ooU d6q26 d8326 cVR1U d81oU buR26 bv1oU cUq26 cUop6 d7QOt cW30t rTW1S rU7Or qrv0r rU7Q4 qsW1S qsUnr qrv1S qsVOr qt7Q4 qrtoS s680r s5UoS s66nr 8PUQu 8PUS7 7ntQu 7oUQu 8Osr7 7oV3V 7nsr7 7nsqV 8Q5r7 90tQu 90u2u 7nu47 3, 2, 1... 7ntS7".split(" "));
        System.out.println("Starting main test...");
        generate();
    }
    
    public void generate()
    {
        final long startTime = TimeUtils.nanoTime();
        for (int x = 0; x < problemStrings.length; x++) {

            //System.out.println("size: " + theSet.size + ", stash size: " + stashCache + ", capacity: " + theSet.capacity);
            System.out.println("attempting to add element " + (theSet.size + 1) + ", " + problemStrings[x]);
            theSet.add(problemStrings[x]);
        }
        long taken = TimeUtils.timeSinceNanos(startTime);
        System.out.println(taken + "ns taken, about 10 to the " + Math.log10(taken) + " power.");
    }

    @Override
    public void render() {
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    public static void main(String[] arg) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        System.out.println("LibGDX Test: ObjectSet<String> crash");
        new HeadlessApplication(new ObjectSetCrash2(), config);
    }
}