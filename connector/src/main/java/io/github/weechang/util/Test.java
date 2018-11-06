package io.github.weechang.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String[] split = {"F", "C", "B", "H", "G", "32", "21", "1", "4", "6", "0", "17", "78", "49"};
        List<String> lstring = new ArrayList<String>();
        List<Integer> lint = new ArrayList<Integer>();
        for (String sp : split) {
            if (sp.matches(("[0-9]*"))) {
                lint.add(Integer.valueOf(sp));
            } else if (sp.matches("[a-zA-Z]+")) {
                lstring.add(sp);
            }
        }
        Collections.sort(lint);
        Collections.sort(lstring);
        CollectionUtils.collect(lstring, new Transformer() {
            public Object transform(Object input) {
                return input;
            }
        },lint);
      System.out.println(lint);
    }
}
