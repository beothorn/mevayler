package com.github.beothorn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static void main( final String[] args ) throws IOException
    {
        final String filePath = "/tmp/foo.mev";
        final Mevayler<List<String>> mevaylerMonad = Mevayler.record(filePath, new ArrayList<>());

        final List<String> mReturn = mevaylerMonad.flatMap(ls -> {
            ls.add("Foo");
            return Mevayler.of(ls);
        }).flatMap(ls -> {
            ls.add("Bar");
            return Mevayler.of(ls);
        }).flatMap(ls -> {
            ls.add("Baz");
            ls.remove("Foo");
            return Mevayler.of(ls);
        }).mReturn();

        mReturn.forEach(System.out::println);

        final ArrayList<String> replay = Mevayler.replay(filePath, new ArrayList<String>()).mReturn();
        replay.forEach(System.out::println);

        final String filePathDo = "/tmp/foodo.mev";
        Mevayler.record(filePath, new ArrayList<>());
        Mevayler.mDo(filePathDo, new ArrayList<String>(),
            (ls) -> {
                ls.add("FooDo");
                return ls;
            },
            (ls) -> {
                ls.add("BarDo");
                return ls;
            }
        ).mReturn();

        final ArrayList<String> replayDo =  Mevayler.replay(filePathDo, new ArrayList<String>()).mReturn();
        replayDo.forEach(System.out::println);
    }
}
