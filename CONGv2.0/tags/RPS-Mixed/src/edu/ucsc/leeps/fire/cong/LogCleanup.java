/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ucsc.leeps.fire.cong;

import edu.ucsc.leeps.fire.cong.server.TwoStrategyPayoffFunction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jpettit
 */
public class LogCleanup {

    private static void write(
            String[] line,
            String[] toAppend,
            Map<Integer, String> intToField,
            Set<String> skip,
            BufferedWriter writer) throws IOException {
        int i = -1;
        boolean firstItem = true;
        for (String datum : line) {
            i++;
            if (skip.contains(intToField.get(i))) {
                continue;
            }
            if (!firstItem) {
                writer.write(",");
            }
            firstItem = false;
            writer.write(datum);
        }
        for (String datum : toAppend) {
            writer.write(",");
            writer.write(datum);
        }
        writer.write("\n");
        writer.flush();
    }

    private static Map<String, Integer> getFieldToIntMap(String[] header) {
        Map<String, Integer> fieldToInt = new HashMap<String, Integer>();
        for (int i = 0; i < header.length; i++) {
            fieldToInt.put(header[i], i);
        }
        return fieldToInt;
    }

    private static Map<Integer, String> getIntToFieldMap(String[] header) {
        Map<Integer, String> intToField = new HashMap<Integer, String>();
        for (int i = 0; i < header.length; i++) {
            intToField.put(i, header[i]);
        }
        return intToField;
    }

    private static Map<Integer, Map<Integer, Integer>> getPairIds(String eventLogFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(eventLogFile)));
        Map<Integer, Map<Integer, Integer>> pairIds = new HashMap<Integer, Map<Integer, Integer>>();
        String line = reader.readLine();
        String[] header = line.split(",");
        Map<String, Integer> fieldToInt = getFieldToIntMap(header);
        int periodIndex = fieldToInt.get("period");
        int idIndex = fieldToInt.get("changedId");
        int isCounterpartIndex = fieldToInt.get("isCounterpart");
        line = reader.readLine();
        int currentPairId = 1;
        while (line != null) {
            String[] fields = line.split(",");
            int period = Integer.parseInt(fields[periodIndex]);
            int id = Integer.parseInt(fields[idIndex]);
            if (Boolean.parseBoolean(fields[isCounterpartIndex])) {
                line = reader.readLine();
                continue;
            }
            if (!pairIds.containsKey(period)) {
                currentPairId = 1;
                pairIds.put(period, new HashMap<Integer, Integer>());
            }
            if (!pairIds.get(period).containsKey(id)) {
                pairIds.get(period).put(id, currentPairId);
                currentPairId++;
            }
            line = reader.readLine();
        }
        return pairIds;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("cleaned-up-ticks.csv")));
        Set<String> skip = new HashSet<String>();
        skip.add("timestamp");
        skip.add("periodStartTime");
        //skip.add("id");
        //skip.add("counterpartId");
        skip.add("hoverStrategy_A0");
        skip.add("hoverStrategy_a0");
        skip.add("counterpartHoverStrategy_A0");
        skip.add("counterpartHoverStrategy_a0");
        skip.add("payoffFunction_name");
        //skip.add("payoffFunction_Aa");
        skip.add("payoffFunction_AaStart");
        skip.add("payoffFunction_AaEnd");
        //skip.add("payoffFunction_Ab");
        //skip.add("payoffFunction_Ba");
        //skip.add("payoffFunction_Bb");
        skip.add("payoffFunction_isCounterpart");
        skip.add("payoffFunction_min");
        skip.add("payoffFunction_max");
        skip.add("counterpartPayoffFunction_name");
        //skip.add("counterpartPayoffFunction_Aa");
        skip.add("counterpartPayoffFunction_AaStart");
        skip.add("counterpartPayoffFunction_AaEnd");
        //skip.add("counterpartPayoffFunction_Ab");
        //skip.add("counterpartPayoffFunction_Ba");
        //skip.add("counterpartPayoffFunction_Bb");
        skip.add("counterpartPayoffFunction_isCounterpart");
        skip.add("counterpartPayoffFunction_min");
        skip.add("counterpartPayoffFunction_max");
        String line = reader.readLine();
        String[] header = line.split(",");
        Map<String, Integer> fieldToInt = getFieldToIntMap(header);
        Map<Integer, String> intToField = getIntToFieldMap(header);
        write(
                header,
                new String[]{"flowPayoff", "counterpartFlowPayoff", "pair"},
                intToField, skip, writer);
        line = reader.readLine();
        int periodIndex = fieldToInt.get("period");
        int millisLeftIndex = fieldToInt.get("millisLeft");
        int idIndex = fieldToInt.get("id");
        //int counterpartIdIndex = fieldToInt.get("counterpartId");
        int pfAaIndex = fieldToInt.get("payoffFunction_Aa");
        int pfAbIndex = fieldToInt.get("payoffFunction_Ab");
        int pfBaIndex = fieldToInt.get("payoffFunction_Ba");
        int pfBbIndex = fieldToInt.get("payoffFunction_Bb");
        int cpfAaIndex = fieldToInt.get("counterpartPayoffFunction_Aa");
        int cpfAbIndex = fieldToInt.get("counterpartPayoffFunction_Ab");
        int cpfBaIndex = fieldToInt.get("counterpartPayoffFunction_Ba");
        int cpfBbIndex = fieldToInt.get("counterpartPayoffFunction_Bb");
        //int isCounterpartIndex = fieldToInt.get("payoffFunction_isCounterpart");
        int p1sIndex = fieldToInt.get("currentStrategy0");
        int p2sIndex = fieldToInt.get("counterpartCurrentStrategy0");
        TwoStrategyPayoffFunction pf = new TwoStrategyPayoffFunction();
        TwoStrategyPayoffFunction cpf = new TwoStrategyPayoffFunction();
        Map<Integer, Map<Integer, Integer>> pairIds = getPairIds(args[1]);
        while (line != null) {
            String[] fields = line.split(",");
            int period = Integer.parseInt(fields[periodIndex]);
            long millisLeft = Long.parseLong(fields[millisLeftIndex]);
            int id = Integer.parseInt(fields[idIndex]);

            if (millisLeft % 1000 != 0
                    || !pairIds.containsKey(period)
                    || !pairIds.get(period).containsKey(id)) {
                line = reader.readLine();
                continue;
            }

            int pairId = pairIds.get(period).get(id);

            pf.Aa = Float.parseFloat(fields[pfAaIndex]);
            pf.Ab = Float.parseFloat(fields[pfAbIndex]);
            pf.Ba = Float.parseFloat(fields[pfBaIndex]);
            pf.Bb = Float.parseFloat(fields[pfBbIndex]);
            cpf.Aa = Float.parseFloat(fields[cpfAaIndex]);
            cpf.Ab = Float.parseFloat(fields[cpfAbIndex]);
            cpf.Ba = Float.parseFloat(fields[cpfBaIndex]);
            cpf.Bb = Float.parseFloat(fields[cpfBbIndex]);
            float A = Float.parseFloat(fields[p1sIndex]);
            float a = Float.parseFloat(fields[p2sIndex]);
            float u1 = pf.getPayoff(0, new float[]{A}, new float[]{a});
            float u2 = cpf.getPayoff(0, new float[]{a}, new float[]{A});
            String[] newFields = new String[]{
                String.valueOf(u1),
                String.valueOf(u2),
                String.valueOf(pairId)
            };
            write(fields, newFields, intToField, skip, writer);
            line = reader.readLine();
        }
    }
}
