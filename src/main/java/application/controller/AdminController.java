package application.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import application.entity.MSetting;
import application.form.OrgForm;
import application.form.SettingForm;
import application.form.UserForm;
import application.service.DivisionService;
import application.service.ListOutputService;
import application.service.OrgService;
import application.service.SettingService;
import application.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * 管理者向け機能用 画面コントローラ。
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    /** パスワードエンコーダー。 */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** モデルマッパー。*/
    @Autowired
    private ModelMapper modelMapper;

    /** ユーザサービス。*/
    @Autowired
    private UserService userService;

    /** 組織サービス。*/
    @Autowired
    private OrgService orgService;

    /** 区分サービス。*/
    @Autowired
    private DivisionService divisionService;

    /** 設定サービス。*/
    @Autowired
    private SettingService settingService;

    /** リスト出力サービス。*/
    @Autowired
    private ListOutputService listOutputService;

    /**
     * ログイン画面を表示する。
     * @return ログイン画面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "admin/login";
    }

    /**
     * 認証情報不一致によるログインエラーを返す。
     * @param model モデル
     * @return ログイン画面
     */
    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String loginError(Model model) {
        model.addAttribute("loginFailedErrorMsg", "ユーザIDまたはパスワードが正しくありません。");
        return "admin/login";
    }

    /**
     * システムの問題によるログインエラーを返す。
     * @param model モデル
     * @return ログイン画面
     */
    @RequestMapping(value = "/login-impossible", method = RequestMethod.POST)
    public String loginImpossible(Model model) {
        model.addAttribute("loginFailedErrorMsg", "現在ご利用できません。");
        return "admin/login";
    }

    /**
     * ユーザ・組織管理画面表示.
     *
     * @return ユーザ・組織管理画面
     */
    @RequestMapping(value = "/user-org")
    public String getUserOrg(Model model) {
        return "admin/user-org";
    }

    /**
     * 組織検索を実行する。
     *
     * @return 組織検索結果
     */
    @RequestMapping(value = "/find-orgs", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findOrgs() {

        return null;
    }

    /**
     * 組織を取得する。
     * @param orgCd 組織コード
     * @return 組織情報
     */
    @RequestMapping(value = "/find-org", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findOrg(@RequestParam(required = true) String orgCd) {

        return null;
    }


    /**
     * ユーザ検索を実行する。
     *
     * @return ユーザ検索結果
     */
    @RequestMapping(value = "/find-users", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findUsers(@RequestParam(required = false) String orgCd) {

        return null;
    }

    /**
     * ユーザを取得する。
     * @param userId ユーザID
     * @return 組織情報
     */
    @RequestMapping(value = "/find-user", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> findUser(@RequestParam(required = true) Integer userId) {

        return null;
    }

    /**
     * 設定画面を開く。
     *
     * @param settingForm 設定フォーム
     * @return 設定画面
     */
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting(@ModelAttribute SettingForm settingForm) {

        // 設定を読み込む。すでに設定しているものがなければ生成する。
        MSetting mSetting = settingService.getSetting().orElse(new MSetting());

        modelMapper.map(mSetting, settingForm); // エンティティからフォームにマッピングする

        log.debug("settingForm : {} :", settingForm);

        return "admin/setting";
    }

    /**
     * 設定情報を登録する。
     * @param settingForm 設定フォーム
     * @param bindingResult バインド結果
     * @param redirectAttributes リダイレクト先にパラメータを渡すためのオブジェクト
     * @return 画面のパス
     */
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public String saveSetting(@ModelAttribute @Valid SettingForm settingForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            log.debug("validate error: {}", bindingResult.toString());
            // 入力チェックエラーを返す
            return "admin/setting";
        }


        MSetting setting = modelMapper.map(settingForm, MSetting.class); // フォームクラスからエンティティクラスにマッピングする
        settingService.registerSetting(setting); // 登録処理を実行

        // 処理成功を返す
        redirectAttributes.addFlashAttribute("updateSuccessMsg", "保存が完了しました");
        return "redirect:/admin/setting";
    }

    /**
     * 組織を登録する。
     * @param orgForm 組織フォーム
     * @param bindingResult バインド結果
     * @return 登録結果
     */
    @RequestMapping(value = "/orgs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerOrg(OrgForm orgForm) {

        return null;
    }

    /**
     * 組織を更新する。
     * @param orgForm 組織フォーム
     * @param bindingResult バインド結果
     * @return 更新結果
     */
    @RequestMapping(value = "/org-update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrgs(@Valid @ModelAttribute OrgForm orgForm,
            BindingResult bindingResult) {

        return null;
    }

    /**
     * 組織を削除する。
     * @param orgCd 組織コード
     * @return 削除結果
     */
    @RequestMapping(value = "/org-delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOrg(@RequestParam(value = "orgCd") String orgCd) {

        return null;
    }

    /**
     * ユーザを登録する。
     * @param userForm ユーザフォーム
     * @param bindingResult バインド結果
     * @return ユーザ登録結果
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @ModelAttribute UserForm userForm,
            BindingResult bindingResult) {

        return null;
    }

    /**
     * ユーザを更新する。
     * @param userForm ユーザフォーム
     * @param bindingResult バインド結果
     * @return 更新結果
     */
    @RequestMapping(value = "/user-update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(@Valid @ModelAttribute UserForm userForm,
            BindingResult bindingResult) {

        log.debug("requested user form: {}", userForm);

        return null;
    }

    /**
     * ユーザを削除する。
     * @param userIds ユーザID
     * @return 削除結果
     */
    @RequestMapping(value = "/user-delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(
            @RequestParam(value = "userIds") List<Integer> userIds) {

        return null;
    }

    /**
     * 組織選択Select2データソースを取得する。
     * @param q 組織名検索ワード
     * @return 組織選択Select2データソース
     */
    @RequestMapping(value = "/orgs/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getOrgSelect2Data(@RequestParam(required = false) String q) {

        return null;
    }

    /**
     * ユーザ選択Select2データソースを取得する。
     * @param orgCd 組織コード
     * @param name ユーザ名検索ワード
     * @return ユーザ選択Select2データソース
     */
    @RequestMapping(value = "/users/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getUserSelect2Data(@RequestParam(required = false) String orgCd,
            @RequestParam(required = false) String name) {

        return null;
    }

    /**
     * 権限選択Select2データソースを取得する。
     * @return 権限選択Select2データソース
     */
    @RequestMapping(value = "/auths/select2", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getAuthSelect2Data() {

    	return null;
    }

    /**
     * リスト出力画面を表示する。
     * @param listOutputForm リスト出力フォーム
     * @return リスト出力画面
     */
    @RequestMapping(value = "/listOutput", method = RequestMethod.GET)
    public String listOutput() {

        return null;
    }

    /**
     * 勤怠情報をCSV形式で出力する.
     * @param outputYearMonth 出力年月(yyyymm)
     * @return CSV形式の勤怠情報
     * @throws JsonProcessingException CSV変換時の例外
     */
    @RequestMapping(value = "/attendance.csv", method = RequestMethod.GET, produces = "text/csv; charset=SHIFT-JIS; Content-Disposition: attachment")
    @ResponseBody
    public Object attendanceCsv(String outputYearMonth) throws JsonProcessingException {

        return null;
    }

    /**
     * パスワードをハッシュ化する。
     * @param passwords 平文パスワード
     * @return ハッシュ化パスワード
     */
    @RequestMapping(value = "/encodePassword", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> encodePassword(@RequestParam(name = "passwords") String passwords) {
        return Arrays.stream(passwords.split(","))
                .collect(Collectors.toMap(
                        password -> password,
                        password -> passwordEncoder.encode(password)));
    }

    /**
     * Validation結果のBindingResultからエラーレスポンスエンティティを生成する。
     * @param result BindingResultインスタンス
     * @return ResponseEntity
     */
    private ResponseEntity<Map<String, Object>> genValidationErrorResponse(BindingResult result) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Map<String, List> errors = result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, error -> new ArrayList<>(Arrays.asList(error)),
                        (a, b) -> {
                            a.add(b);
                            return a;
                        }));

        Map<String, Object> errorRes = new HashMap<>();
        errorRes.put("status", "NG");
        errorRes.put("errors", errors);

        return new ResponseEntity<Map<String, Object>>(errorRes, HttpStatus.BAD_REQUEST);
    }
}
