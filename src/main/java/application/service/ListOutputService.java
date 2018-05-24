package application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.dao.MUserDao;
import application.dao.TAttendanceDao;
import application.entity.MUser;
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

	/** 勤怠情報用DAO **/
	@Autowired
	TAttendanceDao tattendancedao;

	/** ユーザマスタ用DAO **/
	@Autowired
	MUserDao muserdao;

	/** ユーザID */
	private Integer id;

	/** ユーザ氏名 */
	private String name;

	/** 出勤日(dd) */
	private String date;

	/** 出勤時刻(H:mm) */
	private String arrival;

	/** 退勤時刻(H:mm) */
	private String clockout;

/**
 * 勤怠情報をCSV形式に変換するメソッド
 *
 * @param outputYearMonth 出力年月（yyyyMM）
 * @return String CSV形式の勤怠情報
 */
	public String csvDownload(String outputYearMonth) {

		//勤怠情報から出力年月ですべての勤怠情報を取得する
		List<TAttendance> tattendances = tattendancedao.getByAttendanceMonth(CommonUtils.toYearMonth(outputYearMonth));

		//文字列を格納するオブジェクトの作成
		StringBuilder sb = new StringBuilder();

		//ヘッダーを設定する
		sb.append("id");
		sb.append(",");
		sb.append("name");
		sb.append(",");
		sb.append("date");
		sb.append(",");
		sb.append("arrival");
		sb.append(",");
		sb.append("clock-out");
		sb.append("\r\n");

		//勤怠情報を取得する
		for (TAttendance ta : tattendances) {

			//勤怠情報からユーザIDを取得する
			id = ta.getUserId();

			//ユーザマスタからユーザIDでユーザを取得する
			MUser user;
			user = muserdao.getByPk(id);

			//ユーザIDがない場合は出力しない
			if (user == null) {
				continue;
			}
			//ユーザからユーザ氏名を取得する
			name = user.getName();

			//勤怠情報の出勤日から日付のみを取得する
			date = ta.getAttendanceDay();
			date = date.substring(6);

			//勤怠情報から時刻を任意の形式で取得する
			String time = CommonUtils.toHMm(ta.getAttendanceTime());

			//勤怠情報から勤怠区分コードを取得する
			String code = ta.getAttendanceCd();

			//勤怠区分コードに応じてarruvalかclockoutか判断する
			arrival = "";
			clockout = "";
			if (code.equals("01")) {
				arrival = time;
			} else {
				clockout = time;
			}
			//CSV形式の文字列をつくる
			sb.append(id);
			sb.append(",");
			sb.append(name);
			sb.append(",");
			sb.append(date);
			sb.append(",");
			sb.append(arrival);
			sb.append(",");
			sb.append(clockout);
			sb.append("\r\n");
		}
		//文字列を返す
		return new String(sb);
	}
}
