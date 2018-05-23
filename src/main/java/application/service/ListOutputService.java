package application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
@JsonPropertyOrder({ "id", "name", "date", "arrival", "clock-out" })
public class ListOutputService {

	@Autowired
	TAttendanceDao tattendancedao;

	@Autowired
	MUserDao muserdao;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("date")
	private String date;

	@JsonProperty("arrival")
	private String arrival;

	@JsonProperty("clock-out")
	private String clockout;

	public String csvDownload(String outputYearMonth) {

		//データベースに接続する
		//SQLを発行する
		//勤怠情報：ユーザID
		//ユーザマスタ：ユーザ氏名
		//勤怠情報：出勤日
		//勤怠情報：勤怠区分コード
		//勤怠情報：勤怠時刻
		System.out.println(CommonUtils.toYearMonth(outputYearMonth));

		List<TAttendance> tattendances = tattendancedao.getByAttendanceMonth(CommonUtils.toYearMonth(outputYearMonth));

		System.out.print(tattendances.toString());
		StringBuilder sb = new StringBuilder();
		MUser user;

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

		for (TAttendance ta : tattendances) {


			id = ta.getUserId();
			user = muserdao.getByPk(id);
			if(user == null) {
				continue;
			}

			name = user.getName();

			date = ta.getAttendanceDay();
			date = date.substring(6);
			String time = CommonUtils.toHMm(ta.getAttendanceTime());

			String code = ta.getAttendanceCd();
			arrival = "";
			clockout = "";

			if (code.equals("01")) {
				arrival = time;
			} else {
				clockout = time;
			}

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

			System.out.println(sb);

		}
		return new String(sb);
	}
}
