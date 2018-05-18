package application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * リスト出力サービス
 *
 * @author 作成者名
 *
 */
@Service
@Transactional
public class ListOutputService {

    int Test(String s1) {
        int n1 = Integer.parseInt(s1);  //(3)数値に変換
        System.out.println(n1);
        return n1;
    }

//    public static void mian(String[] args) {
//    	ListOutputService los = new ListOutputService();
//    	los.Test("100");
//    }

}
