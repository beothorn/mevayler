package com.github.beothorn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Mevayler<T> {

    private final T data;
    private final List<SerializableFunction<T, Mevayler<T>>> dataTransactions;
    private String filePath;

    public static <U> Mevayler<U> mDo(
        final String filePath,
        final U subject,
        final SerializableFunction<U, U>... functions
    ){
        Mevayler<U> mevaylerMonad = Mevayler.record(filePath, subject);
        for (final SerializableFunction<U, U> function : functions) {
            final Mevayler<U> nextBind = mevaylerMonad.flatMap((subjectBind) -> {
                final U transformedSubject = function.apply(subjectBind);
                return Mevayler.of(transformedSubject);
            });
            mevaylerMonad = nextBind;
        }
        return mevaylerMonad;
    }

    public static <U> Mevayler<U> of(final U subject) {
        return new Mevayler<>(subject);
    }

    public static <U> Mevayler<U> record(
        final String filePath,
        final U subject
    ) {
        return new Mevayler<>(subject, filePath);
    }

    public static <U> Mevayler<U> replay(
        final String filePath,
        final U subject
    ) {
        try {
            final FileInputStream fos = new FileInputStream(filePath);
            try(ObjectInputStream in = new ObjectInputStream(fos)){
                Mevayler<U> mevayler = new Mevayler<>(subject, filePath);
                final List<SerializableFunction<U, Mevayler<U>>> transactions = (List<SerializableFunction<U, Mevayler<U>>>) in.readObject();
                for (final SerializableFunction<U, Mevayler<U>> function : transactions) {
                    mevayler = mevayler.flatMap(function);
                }
                return mevayler;
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Mevayler(
        final T data
    ) {
        this.data = data;
        dataTransactions = new ArrayList<>();
    }

    private Mevayler(
        final T data,
        final String filePath
    ) {
        this.data = data;
        this.filePath = filePath;
        dataTransactions = new ArrayList<>();
    }

    public Mevayler<T> flatMap(
        final SerializableFunction<T, Mevayler<T>> function
    ){
        final Mevayler<T> applyReturn = function.apply(data);
        applyReturn.dataTransactions.addAll(dataTransactions);
        applyReturn.dataTransactions.add(function);
        applyReturn.filePath = filePath;
        return applyReturn;
    }

    public T mReturn() {
        try (final ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(filePath))){
            oo.writeObject(dataTransactions);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
