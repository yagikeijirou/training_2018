package application.form;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import lombok.Data;

/**
 * ユーザ情報登録フォーム
 * @author 黄倉大輔(一部編集)
 */
@Data
public class UserForm {
	@NotNull
	@DecimalMin(value="0")
	@DecimalMax(value="999999")
    private Integer userId;
	@NotNull
	@Size(min=1, max=256)//0531黄倉追記
    private String password;
	@NotNull
	@Size(min=0, max=10)
    private String name;
	@NotNull
	@Email
	@Size(min=0, max=60)
    private String mail;
	@NotNull
	@Size(min=2, max=2)
    private String orgCd;
	@DecimalMin(value="0")
	@DecimalMax(value="999999")
    private Integer managerId;
	@NotNull
	@Size(min=2, max=2)
    private String authCd;
    private String lineId;
}
