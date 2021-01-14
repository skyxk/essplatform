package com.chen.platformweb.service;

import com.chen.dao.IssuerUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static com.chen.core.util.StringUtils.getUUID;

@Service
public class FileServiceImpl implements IFileService{

    @Value("${myConfig.tempFilepath}")
    public String tempFilepath;

//    @Value("${myConfig.fileTypePath}")
//    public String fileTypePath;
    @Autowired
    private IssuerUnitMapper issuerUnitMapper;
    @Override
    public String saveFile(MultipartFile multipartFile) {
        try {
            //获取原始文件名
            String fileName = multipartFile.getOriginalFilename();
            //获取文件类型名
            String fileType = fileName.split("\\.")[1];
            //生成一个UUID作为新的文件名
            String UUID = getUUID();
            //tempFilepath是配置的默认文件存储位置
            String filePath = tempFilepath + UUID+"."+fileType;
            //保存文件
            multipartFile.transferTo(new File(filePath));
            //返回新的文件路径
            return filePath;
        } catch (Exception e) {
            //出现异常后打印并返回null
            e.printStackTrace();
            return null;
        }
    }

//    @Override
//    public List<String> readFileTypeByFile() {
//
//        try{
//            FileReader fileReader = new FileReader(fileTypePath);
//            String result = fileReader.readString();
//            JSONObject js = JSONObject.parseObject(result);
//
//            return null;
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }

    //    @Override
//    public Map<String, String> getCertBase64(Certificate certificate) {
//        try{
//            IssuerUnit issuerUnit = issuerUnitMapper.findIssuerUnitBySM2();
//            String algorithm = certificate.getAlgorithm();
//            if ("1".equals(algorithm)){
//                algorithm = "SHA1withRSA";
//            }else{
//                algorithm = "SM2";
//            }
//            String sC = certificate.getCountry();
//            String sS = certificate.getProvince();
//            String sL = certificate.getCity();
//            String sO = certificate.getUnit();
//            //部门由单位代替（暂时）
//            String sOU = certificate.getDepartment();
//            String sDN = certificate.getCert_name();
//            //起始时间
//            Date dateStart = strToDate(certificate.getStart_time());
//            Date dateEnd  = strToDate(certificate.getEnd_time());
//
//            String sPwd = certificate.getCert_psw();
//
//            return  CreatePfxFile(sC,sS,sL,sO,sOU,sDN,dateEnd,dateStart,
//                    sPwd,algorithm,issuerUnit);
//        }catch (Exception e){
//            return null;
//        }
//    }
}
