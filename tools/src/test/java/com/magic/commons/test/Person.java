package com.magic.commons.test;

import com.magic.api.commons.tools.IPUtil;

/**
 * Person
 *
 * @author zj
 * @date 2017/5/3
 */
public class Person {

    private String name;
    private int age;
    private String address;
    private byte sex;
    private String telephone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte getSex() {
        return sex;
    }

    public void setSex(byte sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


    public static void main(String[] args) {
        String startIp = "127.0.0.0";
        String endIp = "127.0.0.255";

        System.out.println(IPUtil.ipToLong(startIp));
        System.out.println(IPUtil.ipToLong(endIp));

        startIp = "192.168.0.1";
        endIp = "192.168.0.255";
        System.out.println(IPUtil.ipToLong(startIp));
        System.out.println(IPUtil.ipToLong(endIp));
    }
}
