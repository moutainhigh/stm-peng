package com.mainsteam.stm.camera.web.action;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.camera.api.ICameraService;
import com.mainsteam.stm.camera.web.vo.CustomGroupVo;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.util.SecureUtil;

@Controller
@RequestMapping({ "/portal/resource/cameradetail" })
public class CameraDetailAction extends BaseAction {
	private Logger logger = Logger.getLogger(CameraDetailAction.class);

	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;

	@Resource
	private ICameraService cameraService;

	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;

    @Resource
    private MetricDataService metricDataService;

	@RequestMapping({ "/getCameraDetail" })
	public JSONObject getMetric4General(Long instanceId,HttpSession session) {
		ILoginUser user = getLoginUser(session);
		return toSuccess(cameraService.getCameraDetial(instanceId,user));
	}

	@RequestMapping({ "/getCameraConInfo" })
	public JSONObject getCameraConInfo(Long instanceId) {
		return toSuccess(cameraService.getCameraConInfo(instanceId));
	}

	@RequestMapping({ "/getIsCamera" })
	public JSONObject getIsCamera(Long instanceId) {
		Map<String, String> returnObj = new HashMap<String, String>();
		try {
			ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
			if (instance != null && instance.getCategoryId() != null && instance.getCategoryId().equalsIgnoreCase("SurveillanceCamera")) {
				returnObj.put("result", "1");
				logger.info("Is Camera, ID:" +  instanceId +  " name:" + instance.getShowName() + " Result: " + JSONObject.toJSONString(returnObj));
			} else {
				returnObj.put("result", "0");
				logger.info("NOT Camera, ID:" +  instanceId );
			}
			
		} catch (InstancelibException e) {
			logger.error(e.getMessage());
		}

		return toSuccess(returnObj);
	}

	// 判断是否为视频诊断分组及其子分组
	@RequestMapping({ "/getIsCameraGroup" })
	public JSONObject getIsCameraGroup(long id, HttpSession session) {
		int result = 0;
		List<CustomGroupBo> list = cameraService.getList(getLoginUser(session).getId().longValue());
		Map<String, CustomGroupBo> allGroups = new HashMap<String, CustomGroupBo>();
		for (CustomGroupBo bo : list) {
			allGroups.put(String.valueOf(bo.getId()), bo);
		}
		if (id == 999999L) {
			result = 1;
		} else {
			result = isCameraGroup(id, allGroups);
		}
		logger.info("Is Camera Group, ID:" +  id +  " Result: " + result);
		Map<String, String> returnObj = new HashMap<String, String>();
		returnObj.put("result", String.valueOf(result));
		return toSuccess(returnObj);
	}

	public int isCameraGroup(long id, Map<String, CustomGroupBo> allGroups) {
		int result = 0;
		if (id == 999999L) {
			result = 1;
		} else {
			CustomGroupBo cameraGroup = allGroups.get(String.valueOf(id));
			if (cameraGroup.getPid() != null) {
				result = isCameraGroup(cameraGroup.getPid(), allGroups);
			} else {
				result = 0;
			}
		}
		return result;
	}

	@RequestMapping({ "/getAllCustomGroup" })
	public JSONObject getAllCustomGroup(HttpSession session) {
		List<CustomGroupBo> list = cameraService.getList(getLoginUser(session).getId().longValue());

		List<CustomGroupVo> voList = new ArrayList();
		for (CustomGroupBo bo : list) {
			CustomGroupVo vo = toVo(bo);
			voList.add(vo);
		}
		List<CustomGroupVo> newVoList = new ArrayList();
		for (CustomGroupVo vo : voList) {
			if (vo.getPid() == null) {
				voList2Tree(vo, voList);
				newVoList.add(vo);
			}
		}
		return toSuccess(newVoList);
	}

	@RequestMapping({ "/getFileInputStream" })
	public void getFileInputStream(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("instanceId").toString();
		long instanceId = 0L;
		if (id != null) {
			instanceId = Long.parseLong(id);
		}
		Connection con = null;
		Statement stmt = null;
		InputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(response.getOutputStream());
			//获取channId
            MetricData metricData = metricDataService.catchRealtimeMetricData(instanceId, "lastImage");
            String[] data = metricData.getData();
            //logger.error("img####:   "+JSONObject.toJSONString(data));
            if(data != null && data.length > 0){
                String img = data[0];
                //String img = "FFD8FFE000104A46494600010101000300030000FFDB0043000D090A0B0A080D0B0B0B0F0E0D1014211514121214281D1E1821302A32312F2A2E2D343B4B40343847392D2E425942474E50545554333F5D635C52624B535451FFDB0043010E0F0F1411142715152751362E365151515151515151515151515151515151515151515151515151515151515151515151515151515151515151515151515151FFC00011080120016003012200021101031101FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFFDA000C03010002110311003F008BC347FE29FB3FF74FFE846B4B701593E1D3FF0014FD9FFBA7FF0042356E49D1472C054816F78A6EE27A5679BF1D234627D6822EA53CB6C1FAD005C69517EFB0A81EF907114658FB5247628A72E779F7AB31848C6157F01480AE1AEE5F48C1F6A8EEADCC56B24A58BBA8CF35755B7EEDA47CAC54E0F423A8FAD1E5E4E4926988E76559DE4C2648270081C1AB16FA64BE624D23E0A9C815AB322228DA30370CD3A801150F049269FB452D22B2BEEDA41DAC54E3B11D45001452D14844124A89C6D2C7DAA306571818514A4E67C60F14F0F1E59770CAF0C323238CF34C65768308CCEC4902A5B7FF0052B9F4A8DEE079651549278E2A5B6522040DC103A13400E563E6638C6DCE6B3FFE5EDBEB57885F38212376D2719E48E99AA0EE9F6D932E06D70A72D8C1E303EB408D783B55B4AA5132A9505802C76A83DCFA7E9579290128E9581E2B388AD47AB37F2ADF1D2B9EF16FFABB5FAB7F4A00E68F5A6D29A6D31887AD21A29AD4C658B838B5B55FF649FD6A15FBA2A4B83F240BE918A887414087E68CD368CD2285CD14DA298C5A4A292A5885A5CD368A005A29B4B4142D4D631F9DA85AC3FDF9541FA673FD2A0AD4F0C43E6EBF07FD335690F1E83FFAF408EE18F39AA5A9CBE5D84A738246DFCEAD73595AEB95B68D3FBCD9FCA9018FA14377368D6C14ED8F079CFF00B46AF5D585BC56BE75DDCC91C71F2C55B1BBFD9E39E7D052F86F71F0E5A853D9BFF42356AF2C56F0DBB3C922340FE6214DBD7DF208AA20C63E7DB5B47182D0ADEDD08C465B7490C78E9B8F4638E9DAAE69AF753C9A859C333A79451A179D77BC7BC648393CE3DFF5AB5FD98AD1CAB2DC4F2B3B07476600C4C3BA6000BF954B0592DBC72F972482795B7497076B3B1F7C8C71D00C605311870DCDC1F0B5848672659EE7C832B1CB282C7907D6B4B4C9661797967102C238D25856EB3B94B750C7AE075C7E19A920D1E0874E362D34D2439DC9BCAEE8DB39DCA400735347A7C48B26E92579656DD24C5CABB7A0CAE38F61C50063C613CDB1F2ADF5178A76959D84FB7CE2075004800E47B703BD5ABA7974DD06E6E62864B79A5954112CBE69049C6EC9279C7F938152C9A55BC779A7A44B70B18F33389E4C28DBC73BB8C9F4C66AC0D32D364CAC9248B32857F36577271D08C9E08EC7AD2B8185A8DC4D6A9A85BC371711A2DBACA86E58B3AB6F08707AFCDD47D73C55CB2674D71A002682336BBFCA9E43212777DE072D8FA645599748824B6B94B8966B879902B4B2365C05E8010381DFA727AE6A486D1219CDEC934B3CA22F2C3380709D7002819FC8D032A496BE76A57CC2C6CEE3E7886676C11F20E07CA69DA64682CAF542A5A6EB8914F924008063A1C76E7048A9458C33ACF75756B14B3CA778475525540C2AE4F438EBCE3359915BBBD85CDA8531C33BB30555DA429231C638E9D28025124914571A85A4927D9ADE060A66767FB43FF78063C007BF7A7D94F2C37F6317993C82EE2632998E4160A1B727A0F9B1E9C74E2A7B0B5671199AE2599150C4627DBE5B2E31C80067DB3562DB4E8ADE4593CC96578D7644656DDE5AFA2FF8F5F7A04664D1DB3EA17E67B6F36412AE185B19001B178C8069FA5D95B99EF9A34D882440004319FB80F23031D6AD05BF82EEF0C56B1C91CB2065669B6E708A3A6D3E952D9473ACB78F3C6A864914808FB810140EB807B7A5202658D147CAB8AE6B504823B8BF95E4B572B331D8CF1893B71868D8FEB8AE9CEED8C1739C1C6319CFB678FCEB12EADAF1ECA78608AF97CC04ED73060B67AB1CE4FE742019A4456F1EAEBB66B467F29B6885D189E473F2A2F6CF5C9C7B554BC12FF6BDC45E64815AF23C9C0E0E57FD8C71EE47D0F7DAB76BC4BF5768EF9D64F95CCCD10083FBC0277FA7EB59B25AA99A597CB9D94CC1950CB26F65E0027E6E1BA1E7A77C750C658B7B646BCB691ED2DD95B5294198FF00AC720B7046DE9C67A9ABDAB88A38E5C35F8BB72A13CA69CA92581C0C7CB9C678F6A8AD34E5867B2FDCAB4A2769A4B8DB9F94EECA97EA7195EBD7B55F926BA9AF2D18E99726184B4846F887CF8C211F3F6C9FCE9086C2DA6B6AF0C68BA89950178DA7FB415009DA7EF7F33C7AD52F167FACB51ECDFCC568F9D79FDAAB70BA55CEC30885B73C436FCFB89E1CE78ACEF1673716E3D10FF3A6223B34B393C11AD38B41F6C84C44DC39DC4EE603E518F9460763CE6AD810DFB69B6BA3CBA679FF00655492092CD4BC932A92DF33464741D4B5655A6ABF65D327D3C585ACB0DC6DF38C9E66E9369C8C90E318F6C525A6B7258BC12C365662E604291DC042AC3208C900856201EA54FBE682919D08B516F75E789BED014080263686CF3BFDBE95D2E9DA7DB2C7A1DA98AD9E3D462125C99866662E700447AAEDEBC63DF35CD43757105B5D5BC72E22BA50B30C0F980391F4E49E98AB76FAD5CDB5BC31858E492DC30B79E45DD24008E421FE59CE3B6298CD4D2CC6DAE5BE98E34F5B78AE4C0CB3DB6F96E8648FBC5480703D54561ECB18AF3528AE23B88950C8B6F1A3025240C4287249E00E0E09FAD5A8F586B49ADDCD95A4D716CDBD277560F9EB93B580639EEC09AA76F7F7504B792C72E24BC468E76DA3E70C72DDB8C9F4C52035BC397092DCD8D938D3A1537281CCD6E647B8DC79504AB05E0607DD1CFD4D69359DA58791E559DB01797F3452C57637B2C6B26C11A73C37CD9C839E3AE062B02CB55FB1ADB7FA05A4D25AC9E6452C8AC1C1C82012AC370047F166A4835FBC8D9DA78E0BA633B5CC6678F3E4C8D9CB26318EB9C1C8C80719AA195356B58EC358BEB284B18E099A342C7271EF55296491E595E695DA495CEE776392C7D49A6D48051451484145145003696968141615D0783500D46E656FBCB1051F89E4FE95815E93F0BECC2691757AE9FBC9A7650C7AED50071E9CE68EA408ED8535CF6B07CFBF82DD3E6655DC55792067938AF48BDD36D6E10B35BA79B8C29191CF6CEDC1AE7F42B5BEB59F534BED2635740B8BB8F244FEC03761FA55D84A48E77C2C3FE29EB5FA1FFD08D6AD65F85BFE45EB5FA1FF00D08D6A8EB48031542FDF6DE69E8D308619252B23172A00C77208FE75A61B08C3D4564EB3612DF4502C4C80C6F93BC91FD2AA3BEA24F51D322DB24971248A6DD62DEAD14D23E0970141F9C6E07D411DFD2B2EE9677D73FB3E0BE6882B08D99A464553DFEF3127F3FC2BA09E49249E474B7660CD9CFDBA54CFFC040C0FA0AC9BBD26E26D6E4BC478C234E2400939C71ED5B41C6FAD8BB9A2B62B379EEB1EA6144AD02059FA018FDE0DC475E7D47F482D63920D66FADDA4B868E355F2CCCD92C33D47B71FFD7A54B29DAFAE6E2E1A09622D218A378D5C8DD9C0248CA80483819A669567776F2CB25D488E5915142745039C018000F61C50EC93D50EC5F75F948F515147CC6BF4A9CF5A821FB83DAB98816452C8429C13C64F4159EF031C46410436723915A78A42513A90280228A328849EA4E4D494EE187078A5C50032929F4DA402514514C91BFC42A8BFFC7FBFD6AFFF0010AA2FFF001FCDF5A00D287B55C8FAD5487B55C8E8024AE5BC53FF001FF0AFFD33FEB5D4E2B96F157FC8423FFAE23F534C0C06A61A79A652290DA4FE303DE9D4DFF968281849CCCFF5A4A0FDF34B4C04A4A5A4A061494A6928402D252D25488281452D031295464D252AE734EC04D6C8A5D8BF40335EB7E0F845978534E8F61DF2A79981D7E624E4FB722BC9EDB7052D8C835DC7C3DD31527B8D5219258EDD8F9413A8908EE4F43839E9EF4E3B92D68777DE9691982F5CFE03355AFEF63B3D3AE2E8BA81146CDCFA815A58C4F3AF0B7FC8B969F46FFD08D6A565F85BFE45CB4FA37FE846B571599B0B494B452246D1451400EA28A2801B5145F758770C7353D449F7E5FAE7F4A4316B2F5267F34201D780456A9A6FCBD48E7E94C08ED432DBC61B9207353522E29D48A1B8A69A929B41232929D46298C6FF0010AA0FFF001FCDF5AD0FE21545C7FA737D6811A50F6AB91D5387B55C8E90897B5729E29FF9098FFAE4BFCEBACED5C7F8979D55BDA35A00C534C3521A61A0B432917993E94EA6A7DFAA01BDE9C3A53475A7500368A5A4A0621A29692843128A5A2A58094B452D342129571B86EFBB9E68A3AF1408DAD26086E352B3B5DAC629E50AC00C6546491FA62BD6ADE310A88620155500450A008D7B0C0E2BCC3C1B710C7E23B5966188E2475CFF007491807F9D7A66AB72DA7E9371730C4D34A884A22824BB1E074F7A7044B2E77AE5BE204861D08F18F35D62CFAE4F3FCAB1E6BEF1268F736B25D5FEE92E7198E60BE56FFEE64743EE31F8D41E3DD57EDD06996CE860B8466927B76605A32061738E307248AD52B326C41E16FF00916ECFE8DFFA11AD6ACAF0B7FC8B767F46FF00D08D6AD64505253A929084A05145002D251F9939C00052BEE865314A852407043500151FFCB66F75152D44DFF1F0BEEB4862D54BE931088900DE5F25FB8AB86AA5EC7BFCB900F994609CF5A6312C84CB949BAF047B8F5ABB8E2A8DB4FE75D4800C2281804E715A18E2802322931521A65218CA29D4DE94C434FDE1545FFE3F9BEB535C4C430F2D59F9C702A02775D6EC11BB0706811A9176AB91D5487B55D8E9087D71BE21FF0090C4FECAA2BB402B8AF107FC862E3F0FE4280329AA3352374A61A658DA60EA69E6983A1A0045A75201C53A980DA4A752500368A5A4A061494B454805028A51400B476A5A2981A3A42F3238E181001F4AF41D0F51FED2D367D2E799A399A3291C80E0F20F7F515C1E9498B7DDFDE39ADEB3DF0A891495607208AA8A3293D4D34D7BC9D39E3D551C5EDACCB0CAB1E0EFF4700F1D39E871C77AE4BC57791DEEBA648181B748D550818CF724FBE4F35DC4BA6D8F89ADD27958DBDFC631E6C78C91E841EA3FCE6BCC2E0A9B9976BEF018A86C633838AD023A9D4F857FE45BB3FA37FE846B56B2BC2BFF0022DD9FD1BFF4235AD59141452E29714086D029714A0548114CC522665E08E41ACC8616BB692E1E7FDFAE24F9B3F3F3CE2B631CE47A62B21A67B3B59E18CE12460ADC723072314C0D65C919EF8A8DBFD6467EA29D0FFAB07D40A25FBC87D1850508D50CA994A9F14A173D68032B4C5DB7D74BE8456BF6A8E2B6489DDC0F99FA9F5A96981190692A4229A4548C8E936E69F8A4EF4C447F766031D413540FFC7DD68B2FCCADE9C66A84BC5F30A093461ED57A3AA50F6ABB1D004C2B86D73FE42F73FEF7F4AEE45709AC73AA5D7FD7434014314C238A929A7EE9A0B223D299D8D49FC34CA00414B401C53B14C6328A5A31400DC52D2D14804A28A290051452D031293B52D14C2C5AD22F045218643F231E0FA575F18C4614F5AE0A48CAF23A5749A0EA9E7C6B6D29FDEA0C027AB0AA8984E3A5CD992FA4D36DA7B889C6E1195E3D4F02B8551815D16BCE21B2310E3CE704FBE2B9CCD68F60A675FE15FF00916ECFE8DFFA11AD702B23C2BFF22DD9FD1BFF004235AF59162D14A28A910DA5A296818882B335381FECD7055727190072739AD514FDBF9D0056B653F664DC08F94641E08A5B9FF559EC083FAD58C54370BFB893E99A6C029CA28419507D4548ABC50213B53714F2292818CC5348A7371ED4CC93D2818869B91DB934D79A34E19B73FF007579351E2791703F74BFCE9803B2A3832BE0F602A94877DD971919EC7AD5B4B689240492EE7B9E40A82E40FB7FA0C0E9408BF076ABF1D518074ABF1D488916B82D4FFE42573FF5D0D77E2BCFEFF9D42E4FFD356FE75408AA6987EED3CD30FDDA458C6FB95154AFF72A2ED4C072FDDA5A17A52E290C6629714B4500329D4502818DA29D452012929D45031B4869698E6810A665E98A6472B472AC91B157539047AD454A05321AB9ADAA6A1F6E4B618DA635F9C7FB559FC532969B6351D0EC7C2BFF0022DD9FD1BFF4235AF591E15FF916ECFE8DFF00A11AD7A091475A5A294521053A8C52E2900D5EB520A455A70A6018A6B0C861EA29F8A07DE14C6416FCC087D454C3A5456AC161D9C12848DA074E6A4F72420F5CF348435C80BC9C7F5A6F3DB81EA68DC07CB14658F766E948602FCCB26E1FDC1C0A63216755384CC8DEC32299E5CF29F9DB627F7475AB61428C2800534D21914712443E45FC69714ACE89C9602A38E759410A3A77A603676488213C73F4AA77386BBC839C818C739AB8D6F1B941265893C739AA532E2EF6760302813346DC74ABD1D5383B55E8C522490579F5CF37537FBEDFCEBD08579E4DFEBE4FF7CFF3A6344151B54AD51B522C8DFEED47524BDAA314C09074A28ED4B48A1B494B4520128A5A5C5500CA5A5C518A9189452E28AA10DA85FEF54E6A16525AA406714714EF2BDF147978E724FD69922527A52D27A53433B3F0AFFC8B767F46FF00D08D6B0AE67C2F73336956F6C06061886EDF78D7470A955393934104C29E053453C74A42168A51471DF8A6014A3E99A617CF0B827EB4C91578F3E4207F717BFE03934012191377CBF31E9853FD68F791C22FA0A6A6F61B638FCA4F53D7F2A9638153E63991CF7273400C4DDC88630B91F788EBF852F9201DD212EDEFDAA54CBEE5236ED38229DB68023C71D00FA5308A9C8151914C088F0A4FA0CD42776F2A7200EF53B8F948F5E2A0697CB3B705D880401F4A450CFB2A16CB166F4C9A8A251F6A9B6F41E9527953CC7E73B17D05491C2910C28C500376FEF11BD0D50BC5C5FF00E02B4C8E45675EFF00C843F014125FB7ED57E3AA36FDAAF474012FF09FA579C3FDF7FF0078D7A3BF113FFBA6BCE0F3402186A3352B74A8FBD32C865A62D4935323EB4012514B4948684A29D8A4C548D09452E28C550C4A29714629009494EC518A6030A96E07149E591EF566088C84E0676F2695E320E08A9115769F4A8E43DAADEC354E5FBF40588CD14B49DEA90CEA7C224B689067B6EC7FDF46BA05E9583E1A8B1A15A3862321B3EFF31ADB53C50644EB4E180324F1EB5123B30F9463DD8528DAA7EF798FF4E94C44BB89E107E2699854FF00584BBF5DA2936C8FC336D07B29A911153845C0FA52013F7AFF00F4C97D3BFF00F5A9D1C688DF2AFCC7A93DE9D4E5A007807B9FC2A5514C5A78A60443E59E5F700FE94FA6B7FC7CAFFB498FCA958FCA4D05113BA292CF2014E041190722B29DDEE94A4ED8861C9C11C64D5FB183C9B70AAD95EA3B502242299B00E8054D8E29868288A929C69291234F5159B7BFF210FC05699EA2B32F7FE421F80A422FDBF6ABF1F4ACFB7ED5A30D3016E38B39BFDC35E760D7A1DEF16173FF005CCD79EE38A63446D4CEF523547DE9164337DFA6C7D69D2FDF34443AD021F494FC52629142518A7628A0436929D462818DA5C52E29681B1B8A4A7D21E2811A7A35BB3C32C9FED003F0AB325A839CAD5BD221F2F4B881EAC377E7568C408A86CB8EC737736E6352D8E00AC57E5B35D4EB6A20D3DD87572100FAD72CD54B610DA4F4A7525508EABC2BB65F0E42BE9B87D0EE3D2B6628D53B195BFBCDDBFC2B13C1CABFD869F37DE2491DBA9ADB8E43222951B57F3341992ED27FD63607F7453D78E11703E94D551EE69E2810F1F8D482A314F14C43A957AD20390053875A009169D4D5A7503237FF005911F723F4A75365E8A7FBAC29D40C81638D339E87A8C54AACAEB94391DAA3E77B76E3E5E3BD4D81CE0605201A698453E9B4C08C8A6E2A434DA008DFA8ACDBFF00F8FF001FEE8AD27EA2B35EDE3BBF13DADBC99D85371C1F4A092F41DAB421AACE17CCCAF4AB1174A4037523FF0012CB9FFAE66B81AEE7583FF127BAFF0072B8634C6861A60FBC69E6900EB48A2AC9F7A961A69CEFFC6A588714087514B46282C4A297146280128A5C518A062514B8A314C06D2A44F34AB1C6A59D8E001572CB4E9EF4E625F933CB9E82BA2B0D321B31BB25E4C6371EDF4A96C2C488811150630A31C53C8E38A90AD262B23448E6BC52FF00BC82007900B9FE43FAD73C45696B3299F559DC1CA86D8BF4159D5AAD8CD8D3D29BDE9E69BDE988E83C1F26DD3E24F504FEA6B722B88D24788903E624006B99F0D49E5DADA9CE0720FE66B6238564D4246CFDD39C62820D84707B11F5A956A08FA62A65A009052D3053C5310A29EB518EB520A0648B4EA60A50680125FF0054DEC334FEDC73C5237287E94C8CE631F4A00495D1392C1383C9C9A65B5C9941FBB9F6A835289A487E56C639A5B79918C3B1BE7F280914F5041E3F0A00B9494521A000D30D3AA36EB408AB773796A76AEEE9C0E6AAD849E6F8A6DA4DB8C5BBD5C97E55F97D467E955206FF008A951BB7D99E90CD11D455984F15491FA55A88D0221D73FE40B73FEED7126BB2D7DBFE24D37E1FCEB8CA60843D6907434EA6AF4FC691627D98B73DCF414C098C8F4ABA8FF375E9557B934084C518A5A290C4A4A7628C531894629C064E0649F402B66C3409A50B25CB7948790BFC447F4A9032A08649DBCB8A32EDE82B76C3408D7125E1DE7B463A7E3EB5B36F6B05A47B228828F6EA7DCD49537292230AA8BB500007181C62931F853F148682EC466A19E4582DE595BF814B54F595E217D9A714046E9984607AF734584720F93F31392793511EB534A8F1485241822A26AD5198DA4EF4EA6F7A00B9A436DD3603EC7F99AEA2D80F2D6707395E6B95D30634BB7FA1FE66BA6D29C3DA98FB8A441A51F5A941A82DCE631EA38352E6980F06941A60A70A00945482A21520A0436693CB8D9CF6150DBCF24A565DAC21276827919A92E143C454F7AAB68C90DB8B59012E66CC6C3E9C83408D207A53223F263D0E29474A627DE71EF9A0A1CFF3362A9A42C9A84642E5363027B0E98AB9DE9C2810521A426985A818FA8C9A8DA75CE0727D29371239C0F6A0911DB8C75359972FF67D5167EBFBA29F4CD5E95B68CF7AA92466770769E052185A5D74473D3A1F515B1176AC9FB2FCA2AD5B4CC8DE5C87E87D680175F38D264F765FE75C89AEAF5E7074B3FEF8AE5A9822363C537F82909A53F728288C31CE73528FBB51639153AFDDA4032969D8AB961A65CDF38F2D36C7DDDB8029022956A5868B7175F3303147EA57935BD61A25B5A80CCBE6CBFDE6EDF415A54AE52450B3D32D6CC0314796EECDD6AE7CADD3047427AD3B1481428C0E951734B094869D4869944669879A9693B7A0FD6901091CD616B2DE76AD6F06322242F8F735D010335C6DD6A089A8DE4CD192ECD81D8014D1127A125FA45240C643B4A0CA9CD60839AB976669215790801CE00EC6A9152A6B54642D37BD2839FAD14165AD348FEC6B7CF5C1FE66B57469C89F6E7EF76ACAB78DA1D1AC5BF8658D981FF81114EB69DA1B8471C60E69189D747F2CAEBD8F22A6155B3F3238FE2E3F3AB0B4144A29CB518A9169924829E29829D40C43C9C555B9864DC863EAB2AB0FA77AB23EF53E818F1D2A2E970DEEA0D38B62A191FF007AA7D88A42262D8A4F34556927DBD48FCEB3E7BCCEE553F95319A1737823EFF95655C6A0EC4A8E954DE47279CE2A334124CB3CF91E5B6391F8D6E8DEDEDEF58F616B3C9324850F960FDE6E05747143F20A00ACB10EFC9A9367A0AB1E4D1E5FB7E948A2B6CF6151CB16E5C7E231D8D5B31FB534A1A00C4D5666FB1AC0E3E60D9CFAD6376AEA2F2DD668CA38FA1F4AE72E2DDE072AC3E8698228E39A523E5AB022C8E69360A00AC07357ED2C2E2EC85863CFB93815044A12456C03839C1AED346BB82EAD81814215E1D3FBA6A42E4163E1F82001EE0F9D27A1E82B5C280318007B52E71466A0D921318A68EB4B4522830318CF34D34B4940C6F7A4C53F1DE90FF9C5003290FAD2D2628114F519C5BD84D2E482170BF535CFB44BF6782E2442CACA0BAF507FC9E6B4BC44E4C505B0EB338C5587B74D9E59E1635C71568CE472BAC48B25DC688C195133C7BD52C66B467B48EE01BAB21B949F997B8359E3049C763823B8354422074C1C8A17B54A473D29BB79E29946F4168B37C3FD36E1464C3BC9FA17359FF66DC318AE9BC20A975E0CB7B7600828EA47D58D643446373EAA7141916EC99A4B10AC7E641823E957D1BE51F4AA1149943B8153EABDEA6B7931953D14E314017853D6A146CD4AA6802614BDAA3CD1BA801E3834EDF5033851926A9CF7CAA30BC9A065D797009240FC6B3AE6F41618E40E78EF5427BC6638CE49EC2A1DAC57F78760FEE8EF4C44EF70EEDB54FE029BB477627D80C0A886070A303DAAED8E9F757CFF00B94C274323741FE3480820864B999618F00B763C0C56F596871438793F7CFEFC01F415A161A2C167863F3CFF00DF3FC8569A5B53B01412D723A63D854D1DB11D47D2B46383153080628B1467ADA93FC34F5B3CFF000D6822053822A5000E9480CE5D387714EFECD8EAFD14EE050FECDB55C6E8F24F61DCD54BFF000F5B5EDB344AAB03755641C83EE6B6A8A407925E5A4F6172D6B749B244F7C83EE33D6A035E9DAEE8B06B16A518049D398A5C72A7FA8AF38BBB49AD2E5EDEE10A4B19E73D1BD083DC53132B8A92DE79AD6E167B77DAEBF911E86998A314C48ECF4ED462BF8034648907DF43D8FF00855D1D2B82B7964B6984D03049178CF623D08AEB34CD463BE8B83B245FBE9E9F4F6ACA51368CBA1A1F8D140A5A9351B8FAD14EA61A0414C3DE97E9CD1B7D68019CD14EE951492044773D157340CE5B5E98CDA9B0FE18C051F5EF51C7A9C91D84D01C990A6D47CD5491CC9233B1E5892699568C58DB59A5B570F19E71820F423DE96FAE3ED57265F2C46300051CFEBDE9A6986A892322931CD3CD37D2901BFE119CC1A35A37F090C0FF00DF46ADEAE812FB7FF0CA37823A1AA1E19E7C3F687D9BFF004235A176164B44DE40685B209FEE9EB4C82AA0CD595B76C6E538AAFF0068B68F0A655273938E6ADC57D68CBC4ADC71C0CE0D050F5057A9C9F735206E2AB875C961D0F438A4697FBB412583228EF55E6BD0BC0E4D559AE00049ED5993CAF29C2FCA28116EE6F79F99F07B639A8364F37CCDFBB4F5EE69912471FCE41327A9A959CBE32C493D31C9A00726C87E54193FDE34E8A296E5F640A6590F403F9D69E9FE1EBB9F6CB75FE8F1F604E1C8FE95D5D95A5B59288ADD020F51D49F7F5A633134BF0EAAE1EF8F98DFF3CD7EEFE3EB5D0C508450A881547602A411B75FC8D4B115228188B1D4C89428A7AF4AA0154629E052014EA963108C8A01EA3BD2D148028A28A06145145001597AEE8B06AF6C030093A731C9DC7B1F6AD4A2803C9AEAD26B4B878274292A1E54FF0031EA3DEA2C57A4EBBA2C3AB5B6388EE5398E51D41F43ED5C05C5BCB6D70F05CC7E5CC9F79739FC47B55224AB8A7C2EF04A25898ABAF434EC52629891D4E9BA925EC7F77128EA9DBEA3D455DDD5C4248F1CE1D090E3F8ABA5B0D492E53E63B254E597AE7DC0EF59B89B467D0D12D498DC7151BCA91C46576088A32CCC70053A4912146791822A72CCC70147BD41A0A005500678FEF75A3341A8CC881C21750E4160A4F240C6703F11F9D210E6ACCD766F2F4E64070D21DA2B479720202CC7B0AE7FC412F997B0DBA0276AF6EE4D344B662B530D3DC1562AC0820E083DA99566636A36A9298D4C061A43DA9C69BE9480DAF0C7FC8BD69F46FF00D08D69C88AE8C8C32AC3041ACBF0BFFC8BF69F46FF00D08D6C6322990416969616EE08853703905BB5448B0CA6E1A260C7CC24907B76AA5ACC1B241328E0F0699A3CE1277463F2BAE0D06891737EC5E7938AAAF293DF157459C973D0ED1D88AC89F7C52B46DC107068247CB2061F7B9A8E96DE096E65F2E08FCC7F6ED5D2E9FE1D8A0225BE6131EC83EE83EFEB41063E9DA55D6A2D88576C7DE56E07FF005EBADD3746B5D3C0650669FBC8DDBE83B55C8F180AAA140F4A993BFD2A8690F009EB4F0A053569F4CA1C29D803047E229A29F903AD004B1F40474C75A7EE03A9A8233862147CADC64F6353A22F7E4D201464F4F945380C0A5C514861451452105145140C28A28A0028A28A002B2F5BD1A1D5A01D23B98C7EEE5F4F63EA2B528A00F2C9EDE5B5B87B7B84292A1E41FE63DA98457A1EB7A3C5AADB819D9709FEAE4C74F63EA0D707756D35ACED05C4663957A83D08F507B8AA20ADB450B9470C8C51979057B53F6D371F35302CDCEB31DC35ADB5D15484DC46B76C5B6AAA67239ED9233D88C7A1ACADF632D8B59C91E8C27921791AEE4B942E0990F5C29F9B1D81E9DFB5497BBF16EF1C65D92E11F03DB3DFB0F7A9EDE6BB6D41A7121FB6A5A92A727CA66DE3E4C0ED8CFBF7CFA67289719177581657D258C81239B4F166D26E500EC884899DA3A838E38C15E71923144F3DD4972B77E75BA14F324F3C349FBB8A3E1D553660807AE4E5F1DB8DB3CF6F35FDC42D776F2283632799145203925D7F765B80723FFAC7BD55BA86EE6BD9E616177B2E4B2CAA4C2311B040C07EF39C88F1DBEF67B7306A8EA346593CA4379E5F9FB0B33424AC6011D06EC9FCC8FA7159579743FB52DE03A86A167E6242BE4C63E54CF182770E7FE03F9D5BB7BC9069E5EE53EC85D1C3472383B0EC200DC0E08F4AC44BC9BC9B3FB2EB42D8476D0AF92CD28C328E785520F3F9D52259977E317F723CC925C4ACBE649F79B0719355AACDEAC6B7527953ACEAC77798AA5412793C1031CD56AAB1030D21A71A6D0030D3476A71A41D68035BC33C6836BF46FF00D08D6D2F4AE5744D674DB5D260826BA092203906363DC9EC2B513C49A381CDF2FF00DFB7FF00E268332F5F41E75BBA7A8E2B9D8898E4C8EC6B58F88F433FF3111FF7EA4FFE26B1A6D43497BC629A822C44E77796E71F86DA46917A9BF652DD5C19446A026477E953268267B8125C4984EE8BD4FE3552C7C41E1FB381625D4060753E549CFF00E3B5763F16787F3CEA4A3FED8C9FFC4D329A372D2DE0B588470461147A558DB9AC25F1778747FCC5147FDB097FF89A9078C7C363FE628BFF007E25FF00E26A883763E0E2A65EB5CE9F19786B191AB2E7D3C897FF0089A7A78D3C35DF5641FF006C65FF00E22803A414B9AE7BFE136F0CFF00D05D3FEFC4BFFC451FF09B78633CEACB8FFAE12FFF0013401D1A9CD489193C9E95CE2F8E7C2AA3FE42C3FF0001E5FF00E269E3C7BE161FF3161FF7E25FFE26803A655E31DAA45E0D72FF00F09FF85BFE82A3FEFC4BFF00C4D2FF00C2C0F0B7FD0547FDF897FF0089A00EA68AE5C7C41F0AF7D57FF204BFFC4D2FFC2C1F0A7FD057FF0025E5FF00E26A4674F45731FF000B07C29FF415FF00C9797FF89A3FE160F853FE82BFF92F2FFF0013401D3D15CC7FC2C1F0A7FD057FF25E5FFE268FF8583E14FF00A0AFFE4BCBFF00C4D0074F45731FF0B07C29FF00415FFC9797FF0089A3FE160F853FE82BFF0092F2FF00F13401D3D15CC7FC2C1F0A7FD057FF0025E5FF00E268FF008583E14FFA0AFF00E4BCBFFC4D0074F45731FF000B07C29FF415FF00C9797FF89A3FE160F853FE82BFF92F2FFF0013401D3D66EB3A443AA5BED6F9275E63947553FE1ED595FF000B07C29FF415FF00C9797FF89A3FE160F853FE82BFF92F2FFF0013401CD5C5BCB6B70F6F3AEC957AA9EE3D47A8AAE56B7358F15783B54B7C1D5764EA3F7728B7972A7DFE5E47B572E75BD27A1BF427B958A4C1FA656A9325A2CE1C1F6FAF3484104329C1154CEB7A4FFCFE0FFBF6FF00FC4D30EB5A6FF0DE0FFBF6FF00E1542B1D2E9D7E5B11C870C3BD6A6ECD70BFDB7A68FF0097A19FFAE6FF00E157ECBC57A744024D76081DF63FF8565245A347C4371911DBA9F76FE95898A6DF6BFA6DD5D3CBF6BE3A01B1B81F95563ABE9BFF003F43FEF86FF0A10EE59614CAAA756D3CFF00CBC8FF00BE1BFC293FB534FF00F9FA1FF7C37F855125934DAAC753B0FF009F91FF007C37F8537FB4EC7FE7E47FDF0DFE14864ED40EB559B52B23FF002DC7FDF2DFE148351B307FD78FFBE5BFC2908FFFD9";
                //logger.error("imgString###@: " +img);
                //byte[] bytes = img.getBytes();
                byte[] bytes = this.toBytes(img);
                bis = new ByteArrayInputStream(bytes);
                byte[] b = new byte[10 * 1024];
                while (bis.read(b, 0, 10240) != -1) {
                    bos.write(b, 0, 10240);
                }
                bos.flush();
            }
            /*ResourceInstance camera = resourceInstanceService.getResourceInstance(instanceId);
			String channelId = camera.getDiscoverPropBykey("channelId")[0];
			con = getConnection(camera);
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select vqdImage from v_vqd_lastresult where channelId = '" + channelId + "'");// 返回SQL语句查询结果集(集合)
			while (rs.next()) {
				bis = rs.getBinaryStream(1);
				byte[] b = new byte[10 * 1024];
				while (bis.read(b, 0, 10240) != -1) {
					bos.write(b, 0, 10240);
				}
				bos.flush();
			}*/
			return;
		} catch (Exception e) {
			logger.error("Get Camera Picture Failed:" + e.getMessage());
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				logger.error("Close IO Exception:" + e.getMessage());
			}
		}
	}


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    private  byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

	@RequestMapping({ "/getalarmFileInputStream" })
	public void getalarmFileInputStream(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("instanceId").toString();
		//String timestamp = request.getParameter("time").toString();
		long instanceId = 0L;
		if (id != null) {
			instanceId = Long.parseLong(id);
		}
		Connection con = null;
		Statement stmt = null;
		InputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			//Date date = new Date(Long.parseLong(timestamp));
			//String startTime = df.format(date) + ".000";
			//String endTime = df.format(date) + ".999";
			bos = new BufferedOutputStream(response.getOutputStream());
            MetricData metricData = metricDataService.catchRealtimeMetricData(instanceId, "lastImage");
            String[] data = metricData.getData();
            if(data != null && data.length > 0){
                String img = data[0];
                byte[] bytes = this.toBytes(img);
                bis = new ByteArrayInputStream(bytes);
                byte[] b = new byte[10 * 1024];
                while (bis.read(b, 0, 10240) != -1) {
                    bos.write(b, 0, 10240);
                }
                bos.flush();
            }
			//ResourceInstance camera = resourceInstanceService.getResourceInstance(instanceId);
			//String channelId = camera.getDiscoverPropBykey("channelId")[0];
			//con = getConnection(camera);
			//stmt = con.createStatement();
			//ResultSet rs = stmt.executeQuery("select vqdImage from v_vqd_result where channelId = '" + channelId
			//		+ "' and (vqdTime between '" + startTime + "' and '" + endTime + "')");// 返回SQL语句查询结果集(集合)
			/*while (rs.next()) {
				bis = rs.getBinaryStream(1);
				byte[] b = new byte[10 * 1024];
				while (bis.read(b, 0, 10240) != -1) {
					bos.write(b, 0, 10240);
				}
				bos.flush();
			}*/
			return;
		} catch (Exception e) {
			logger.error("Get Alart Camera picture failed:" ,e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				logger.error("Close IO Exception:" ,e);
			}
		}
	}

	public Connection getConnection(ResourceInstance camera) {
		Connection con = null;
		try {
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			String userName = camera.getDiscoverPropBykey("dbUsername")[0];
			String password = SecureUtil.pwdDecrypt(camera.getDiscoverPropBykey("dbPassword")[0]);
			String ipAddress = camera.getDiscoverPropBykey("IP")[0];
			String jdbcPort = camera.getDiscoverPropBykey("jdbcPort")[0];
			String dbName = camera.getDiscoverPropBykey("dbName")[0];
			String url = "jdbc:microsoft:sqlserver://" + ipAddress + ":" + jdbcPort + ";databaseName=" + dbName;
			con = DriverManager.getConnection(url, userName, password);
		} catch (ClassNotFoundException e) {
			logger.error("Get JDBC driver failed, please check "+ e.getMessage());
		} catch (SQLException e) {
			logger.error("Get SQL Server connection failed, please check "+ e.getMessage());
		}
		return con;
	}

	public CustomGroupVo toVo(CustomGroupBo bo) {
		CustomGroupVo vo = new CustomGroupVo();
		vo.setId(bo.getId());
		vo.setName(bo.getName());
		vo.setResourceInstanceIds(bo.getResourceInstanceIds());
		vo.setEntryId(bo.getEntryId());
		vo.setEntryDatetime(bo.getEntryDatetime());
		vo.setGroupType(bo.getGroupType().toString());
		vo.setPid(bo.getPid());
		return vo;
	}

	private void voList2Tree(CustomGroupVo parentVo, List<CustomGroupVo> voList) {
		List<CustomGroupVo> childCustomGroupVo = new ArrayList();
		for (int i = 0; i < voList.size(); i++) {
			CustomGroupVo vo = (CustomGroupVo) voList.get(i);
			if ((vo.getPid() != null) && (parentVo.getId().longValue() == vo.getPid().longValue())) {
				childCustomGroupVo.add(vo);
				voList2Tree(vo, voList);
			}
		}
		parentVo.setChildCustomGroupVo(childCustomGroupVo);
	}
}
