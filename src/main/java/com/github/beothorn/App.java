package com.github.beothorn;

import static com.github.beothorn.Mevayler.mDo;
import static com.github.beothorn.Mevayler.record;
import static com.github.beothorn.Mevayler.replay;
import static com.github.beothorn.Mevayler.result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static void main( final String[] args ) throws IOException
    {
        final String filePath = "/tmp/foo.mev";
        final List<String> mReturn = record(filePath, new ArrayList<String>())
        .flatMap(ls -> {
            ls.add("Foo");
            return result(ls);
        }).flatMap(ls -> {
            ls.add("Bar");
            return result(ls);
        }).flatMap(ls -> {
            ls.add("Baz");
            ls.remove("Foo");
            return result(ls);
        }).mReturn();

        mReturn.forEach(System.out::println);

        final ArrayList<String> replay = replay(filePath, new ArrayList<String>()).mReturn();
        replay.forEach(System.out::println);

        final String filePathDo = "/tmp/foodo.mev";
        mDo(filePathDo, new ArrayList<String>(),
            (ls) -> {
                ls.add("FooDo");
                return ls;
            },
            (ls) -> {
                ls.add("BarDo");
                return ls;
            }
        ).mReturn();

        final ArrayList<String> replayDo =  replay(filePathDo, new ArrayList<String>()).mReturn();
        replayDo.forEach(System.out::println);
    }
}
