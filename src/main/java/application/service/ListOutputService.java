package application.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.entity.TAttendance;
import application.utils.CommonUtils;

/**
 * リスト出力サービス
 *
 * @author 荒木麻里
 *
 */
@Service
@Transactional
public class ListOutputService {

	@Autowired
	TAttendanceDao tattendancedao;
	MUserDao muserdao;

	public void csvDownload(HttpServletResponse response, String outputYearMonth) {

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

		List<TAttendance> tattendances = tattendancedao.getByAttendanceMonth(outputYearMonth);

		try (PrintWriter pw = response.getWriter()) {
			{
				String lineSepa = System.getProperty("line.separator");
				for (TAttendance ta : tattendances) {

					Integer id = ta.getUserId();
					String name = muserdao.getByPk(id).getName();

					String date = ta.getAttendanceDay();
					date = date.substring(6);
					String time = CommonUtils.toHMm(ta.getAttendanceTime());

					String code = ta.getAttendanceCd();
					String arrival = "";
					String clockout = "";

					if (code.equals("01")) {
						arrival = time;
					} else {
						clockout = time;
					}
					//CSVファイル内部に記載する形式で文字列を設定
					String str = id + "," + name + "," + date + "," + arrival + "," + clockout + "," + lineSepa;

					//CSVファイルに書き込み
					pw.print(str);
				}
			}
		} catch (IOException e) {
			System.out.println("Exception :" + e.getMessage());
		}
	}
}
