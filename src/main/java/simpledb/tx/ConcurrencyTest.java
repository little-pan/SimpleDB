package simpledb.tx;

import simpledb.buffer.BufferMgr;
import simpledb.file.*;
import simpledb.log.LogMgr;
import simpledb.server.SimpleDB;

public class ConcurrencyTest {
   static SimpleDB db;
   private static FileMgr fm;
   private static LogMgr lm;
   private static BufferMgr bm;

   public static void main(String[] args) {
      //initialize the database system
      db = new SimpleDB("concurrencytest", 400, 8);
      fm = db.fileMgr();
      lm = db.logMgr();
      bm = db.bufferMgr();
      A a = new A(); new Thread(a).start();
      B b = new B(); new Thread(b).start();
      C c = new C(); new Thread(c).start();
   }

   static class A implements Runnable { 
      public void run() {
         try {
            Transaction txA = new Transaction(db, fm, lm, bm);
            BlockId blk1 = new BlockId("testfile", 1);
            BlockId blk2 = new BlockId("testfile", 2);
            txA.pin(blk1);
            txA.pin(blk2);
            System.out.println("Tx A: read 1 start");
            txA.getInt(blk1, 0);
            System.out.println("Tx A: read 1 end");
            Thread.sleep(1000);
            System.out.println("Tx A: read 2 start");
            txA.getInt(blk2, 0);
            System.out.println("Tx A: read 2 end");
            txA.commit();
         }
         catch(InterruptedException e) {};
      }
   }

   static class B implements Runnable {
      public void run() {
         try {
            Transaction txB = new Transaction(db, fm, lm, bm);
            BlockId blk1 = new BlockId("testfile", 1);
            BlockId blk2 = new BlockId("testfile", 2);
            txB.pin(blk1);
            txB.pin(blk2);
            System.out.println("Tx B: write 2 start");
            txB.setInt(blk2, 0, 0, false);
            System.out.println("Tx B: write 2 end");
            Thread.sleep(1000);
            System.out.println("Tx B: read 1 start");
            txB.getInt(blk1, 0);
            System.out.println("Tx B: read 1 end");
            txB.commit();
         }
         catch(InterruptedException e) {};
      }
   }

   static class C implements Runnable {
      public void run() {
         try {
            Transaction txC = new Transaction(db, fm, lm, bm);
            BlockId blk1 = new BlockId("testfile", 1);
            BlockId blk2 = new BlockId("testfile", 2);
            txC.pin(blk1);
            txC.pin(blk2);
            System.out.println("Tx C: write 1 start");
            txC.setInt(blk1, 0, 0, false);
            System.out.println("Tx C: write 1 end");
            Thread.sleep(1000);
            System.out.println("Tx C: read 2 start");
            txC.getInt(blk2, 0);
            System.out.println("Tx C: read 2 end");
            txC.commit();
         }
         catch(InterruptedException e) {};
      }
   }
}
