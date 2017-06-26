package com.magic.api.core;

import com.magic.api.commons.core.tools.MauthUtil;

public class MauthUtilTest {

    public static void main(String[] args) {
        //String mauth = "MAuth c51018eb4b543712cfd96e8a898cbf2d49a210a8fa2644a3cb8e785dfc0a5158";
        //System.out.println(mauth.length());
        //System.out.println(MauthUtil.getUid(mauth).getUid());
        createOldMauth();
    }

    public static void createOldMauth() {
//        int uid = 105094;
        int uid = 7339598;
        String old = MauthUtil.createOld(uid, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 365));
        System.out.println(old);
        System.out.println(MauthUtil.getUid(old).getUid());
    }
}
