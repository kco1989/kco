package com.kco.proto.demo1;

import com.google.protobuf.ByteString;
import com.kco.proto.utils.HexOutputStream;
import proto.EncodingProto.*;
import proto.Helloworld;
import proto.PersonProto;
import proto.PersonProto.Person;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/5/5.
 */
public class PersonTest {
    public static void main(String[] args) throws Exception {
        Test1 t1 = Test1.newBuilder()
                .setA(300)
                .build();
        t1.writeTo(new HexOutputStream());

//        System.out.println(Integer.toHexString(2111));
    }

    private static void test() throws java.io.IOException {
        Person p = Person.newBuilder()
                .setId(11234)
                .setName("11Jack")
                .setEmail("11xx@xx.com")
                .setName1("name1")
                .setName2(5)
                .putProject2("a", 1)
                .putProject2("b", 2)
                .putProject2("c", 3)
                .putProject2("a", 3)
                .build();
        p.writeTo(new FileOutputStream("D:\\1.txt"));
        Person person = Person.parseFrom(new FileInputStream("D:\\1.txt"));
        System.out.println(person);
        System.out.println(person.getName1());
    }
}
