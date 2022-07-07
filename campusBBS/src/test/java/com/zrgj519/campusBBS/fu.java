package com.zrgj519.campusBBS;

import org.junit.Test;

// 验证子类是否继承了父类的私有属性
   public class fu{
        private char sex=1;
        private int age=1;
        private void priFun(){
            System.out.println("this is private method from fu");
        }

        public int getAgeAdd(){
            System.out.println(this.getClass().getName());
            return ++age;
        }

        public void getPriFun(){
            priFun();
        }

        public char getSex() {
            return sex;
        }

        public void setSex(char sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

 class Zi extends fu{

     public static void main(String[] args) {
         Integer a = 126;
         Integer b = 126;
         Integer a1 = 129;
         Integer b1 = 129;
         System.out.println(a == b);
         System.out.println(a1 == b1);
     }
    }




