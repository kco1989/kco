//package com.kco.proto.demo2;
//
//import io.grpc.stub.StreamObserver;
//import proto.GreeterServiceGrpc;
//import proto.Helloworld;
//
///**
// * Created by Administrator on 2017/5/5.
// */
//public class GreeterImpl extends GreeterServiceGrpc.GreeterServiceImplBase{
//    @Override
//    public void sayHello(Helloworld.HelloRequest request, StreamObserver<Helloworld.HelloReply> responseObserver) {
//        System.out.println("say Hello");
//        String name = request.getName();
//        Helloworld.HelloReply reply = Helloworld.HelloReply.newBuilder()
//                .setMessage("Hello " + name)
//                .build();
//        responseObserver.onNext(reply);
//        responseObserver.onCompleted();
//    }
//}
