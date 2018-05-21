package application.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * リスト出力サービス
 *
 * @author 荒木麻里
 *
 */
@Service
@Transactional
public class ListOutputService {

	public void csvDownload(HttpServletResponse response) {

		//文字コードと出力するCSVファイル名を設定
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\"yyyymm.csv\"");

		//データベースに接続する

		//SQLを発行する
		//勤怠情報：ユーザID
		//ユーザマスタ：ユーザ氏名
		//勤怠情報：出勤日
		//勤怠情報：勤怠区分コード
		//勤怠情報：勤怠時刻

		//CSVファイル内部に記載する形式で文字列を設定
		String lineSepa = System.getProperty("line.separator");
		String id = "aa";
		String name = "bb";
		String date = "cc";
		String time = "dd";
		String code = "01";
		String arrival = "";
		String clockout = "";

		if (code.equals("01")) {
			arrival = time;
		} else {
			clockout = time;
		}
		String str = id + "," + name + "," + date + "," + arrival + "," + clockout + "," + lineSepa;

		//CSVファイルに書き込み


		try(BufferedWriter bw = new BufferedWriter(new FileWriter("yyyymm.csv"))) {
			bw.write(str);
		} catch (IOException e) {
			System.out.println("Exception :" + e.getMessage());
		}

	}
}
