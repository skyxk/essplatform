package com.chen.platformpdf.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chen.core.bean.HttpResult;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chen.core.util.Base64Utils.ESSGetBase64Decode;
import static com.chen.core.util.FileUtils.fileToByte;
import static com.chen.core.util.HttpHelper.executePost;


/**
 * @author ：chen
 * @date ：Created in 2019/10/14 10:01
 */
public class test {
    public static void main(String[] args) throws IOException, DocumentException {
        HttpResult ht1 =null;
        HttpResult ht2 =null;
        Map<String,String> map = new HashMap<>();

        String Plain = "批准";
        JSONObject js = new JSONObject();
        js.put("sysId","SYS001");
        js.put("sysPersonID","cltess");
        js.put("plain",Plain);
        js.put("plainEncodeType","UTF-8");

        map.put("data",js.toJSONString());
        try {
            ht1 = executePost("http://localhost:8081/sign/web/sign", map, "UTF-8", 300000);
            JSONObject js1 = JSONObject.parseObject(ht1.getContent());
            System.out.println(js1);
            System.out.println("****************************");
            JSONObject js2= new JSONObject();
            js2.put("encodeData",js1.getString("encodeData"));
            js2.put("imageData",js1.getString("imageData"));
            js2.put("plain",Plain);
            js2.put("encodeType","UTF-8");
            js2.put("imgType",0);
            js2.put("signSerialNum",js1.getString("signSerialNum"));
            js2.put("wantEncodeType","UTF-8");

            Map<String,String> map1 = new HashMap<>();
            map1.put("data",js2.toJSONString());

            ht2 = executePost("http://localhost:8081/sign/web/verify", map1, "UTF-8", 300000);
            JSONObject js3 = JSONObject.parseObject(ht2.getContent());
            System.out.println(js3);
            System.out.println("****************************");

        } catch (IOException e) {
            e.printStackTrace();
        }

//        long l1 =System.currentTimeMillis();
//        for (int i = 0;i<1000;i++){
//            FileOutputStream os  = new FileOutputStream(new File("D:\\pdf\\0b809b58-405e-4bec-b297-c4622afd2cc7\\temp1.pdf"));;
//            byte[] pdfFile = File2byte("D:\\pdf\\0b809b58-405e-4bec-b297-c4622afd2cc7\\temp.pdf");
//            byte[] pfxFile = File2byte("D:\\pdf\\0b809b58-405e-4bec-b297-c4622afd2cc7\\root.pfx");
//            byte[] imgFile = File2byte("D:\\pdf\\0b809b58-405e-4bec-b297-c4622afd2cc7\\seal.gif");
//            addSeal(pdfFile,imgFile,60,30,1,300,500,pfxFile,"111111","112312",os);
//            os.close();
//        }
//        long l3 =System.currentTimeMillis();
//        System.out.println((l3-l1)/1000);

//        File pdfFile = new File("D:\\temp\\demo4.pdf");
//        PDDocument document = PDDocument.load(pdfFile);
//        int pageNum = document.getNumberOfPages();
//        System.out.println(pageNum);

//        File file  = new File("D:\\myCloud\\诚利通\\印章图片\\透明演示章.gif");
//        String aaa = "R0lGODlh8AHwAYAAAP8AAP///yH5BAEAAAEALAAAAADwAfABAAL/jI+py+0Po5y02ouz3rz7D4biSJbmiabqyrbuC8fyTMPAjef6zvd6DQwKh8SiseNLKpfM3fEJjUqnVEbzis0yq9yu9wumaMfksi+MTqvXNrP7Dcex5/S63RrP6+H3vv8PtSc4yAdoeIhoQrjIWJj4CBmJ10hZWSaJmelnydlppgka2uVJWjomipoaZMraiqUKG4viSlu7JYubi2HL25ukCxys4Ht7QrwknCx7LGfE3KwcLUmMRi19Dcj7p43dzUaLCe497sUqBdDSSr7+ZLpywwH/4s5eX0M6j66RM4Nv/5+OU789QDoBPKjIUkFBQgwifBiv0pBBRQRCvCiGkjOG/0cUYvw4aVEgguc0gjzZaAq/XSuppDx5kRGXlhZ+lBMJE6DMUdBq0rxJMec6nF84ViMktBtRMEbVIE2qLGgEeRv11JEKNRdWCD8bknQpYWtWVGK5dl1odWIDqlObjgXlNqwTImfl9lw4yWfat+H26uUx8W5GwQP1DSNsNg/fSF8vIFuFuG3kNmwN2HTsd/E2xRuUBK48GLSMywfmDpPsSPOdzCx7VGxMw3QAwAsMJ06t+lscJL9esx5NmnaCyYdx5w7DOaLrjrCB/5SNgHjt3ceRU1cuvGry2M9JR6/74Hr1Kts7Lx+Z2TYL6N5Lt7f7ZrxL4xl6RwF/W73oWd3rZv+vIJ582l3ywRnz7SdZcS6w599/f30iIF301XeeSghONZ10IbT33mwOYuZGhF6FWKB94eElQocleNdhhebFJ6JzJHrg2ofuqbeiOClw2CB+FM4YY0BAYmeTjR5qyJs6KrD4HnQgTBhkjhBiNpyLTt6I5ItKGsMkYUbSOGSUJEAJn4mWqfhliVsmdNeVV6YIo5gjxLmPZyHheGaaSV7IVJeivTlmmHISeQqYacr22II0cicYjypatqGgg7ZGRqSPftdMovkoV1hlTbro3pySTlpmoZbyiWkT3GEXA4ON2pknnntWSuqDWkiJaqoG3hPXkv21SWugBNZaahZsIqkpr73/cvkrjsMKGyyxDoyqJqrJotUcf8A2a1gxx94q7VrPfivrtaUx+huz23r6KajQmhouluD6+qq5ecp4p7hZVussTab1Cyic4xJLrag9GZthrgaXC96lC+sXXEteOnzqvAQPTK+qsTJM8an5fvyOn7b9e6GPAsM76ZQLaiwvnnqeDHJxCvNbpcQRy/rdytHKqbLOZspbc8DQxhw0zg9DDI2rRneKMM87U/alE/aSS+bJSCfdlckyNh0kxvnoaXHG5ZFbNNAyoxe2gF5DvbSu3oodINm6bky0hCiPtzbbxRbZMb9xv3s1PGhqzfQraj+tLFUuw0r3esvCqa/g+PW9teHV/+W9dcvmUr5n3WZXfuTSnFd+xXGIQ2bz1I1n7PnqxkSOjsmjk84yX6ePqDjjsDu+3+D7Uninw0LbnXZSt+OeO20Nz05p66G3nWTCgpfK0905Hf9Z8vVO/Hud2QosPYDMF167UNhnP3LW/41va9WzFg2920VZ/9H56Hd7lpuEE+o+oXOLvz/iWQ4l9EMb/tSXvwB6L11+k8PshteOAh7Efr7BGvf+xL4JlOVJugPgzAoiswFChILdU9cBt8exD/4ohTjLYH52FRoVFiaEpYuJBOGHLe0B7IIy1Mvu8OAhqv0sPArslPRqiBAKbqyIFTvh4hDjQrNMy0dMLJPmOlZFvf8F7231UOISo7jC9KHwiSXkIHGySERuDa+MbDPaDa/hxSvGD1c6DJ8dWzU5MJYNfjA8yqXeGA1AHrGHVmtZCJ23wKXQjI9DnGOO9pZGro3Deh962Y78hUAeOnJvj6PUDuWhKTYa6oXSieMyigcqS14ydShEZBifssj/uW2IIZtMH2FXPjgW74tiROPDZPk5HDZxg578JA1pCbcttk91wLihcFRZS7Y4CkF6TNUURaklYzLyltHMZBkFCQtAAgaa3exl4FKYOQDNSpuy5GY5pUnOWTIzFuDkIurg2UogplODm6yb0thVzWLq4xcBBKcodonLgFZMfsCsEtNCQ6RD7nH/lokz6PNyiQuEXhOCoAvm89aCLkf6cpoAXRc2F4UMd5Gyg6cUYQz/hD6G8jKehaReGCXa0I8yaqZOnJ4HMaoKCULRQQoFUUnN6VJ+mqOYOPWoTtt4RRzKUKNwSZu7zERTMx5Vnkj8qT+IOFGnkhSPJHMSP4pI1UygkqiJymoslyjHriqVHpGM3VjD6jq48S2VkvudRQ+B0ApJTXkc/dZE5yJJTnpko2zlljBX2RuW9tWvia1qUvkI1y/y8qEM/ScyE+oQxprVm00V4jiTNVBypjURq71oYydb2F8GzZCDfCBzTDRak/bTqLBUKWg/+4jWurZH9hSbgmj7293e1k53/80p4Fi4Evu4M7m+ZW1lRWvL0xYVPsfNbF1POqBrNbeilzkPS7ELXEMIl6Lf1ZwWo/PYF26mg/9EHqIQm6nYrtcO112paDHrs7ZxLrbzwyppczjZPZYXv6LrbzYuu9LetcuXvM2PUQHLXJEp12onPFvotmktCM9XrooFjY3c6sn2Uim4JN6urXLqQE0iS8SrcTAuXcteZfKuwRSGRC5d/FK8QqpFltxvGmwcPPfi+Lc7rqtSdAfkEy0ZuR4+5syM3CcSo+ao0x0ueLl740lmOGmOmzKVnZterlZ3DkbW32eB6rEfDsVwX44wGVs4xtEh2Sk0PmJy/ztcbbmxx6lQVf+UeernO8KZuiguiZa/O+iAPbPRt5GzTvrsvyDe0cPzhHSas/zohEIau8esmZosPcFFr5PHQw21V8/LZ1cPctSinvWZQeRKe3T6ldltdXEF+unqqRrVXi6ZpMuaVR6ar8vZLOuNQLxmXgebPLLGkJWN/SjBMjsvpSUggecKW7t+r4HTdvSwpU1q0G6zNd1edp1l+hJB79pC1W62hLNtJbduTzXv9i5PMcdhWAt73onMM6C5vOEjzdZ0TfQzwJ9b7vAKXKuODTOkZMpuh8bL06WWo1P1GnEBEnzVt8Y4DXmz8WWamKOHRu+2DThykh87j4SeTcpfmluttdzllKbdy4//NvNe9/vmNs15KWsO7J9XMOYUxzfNh050WvtbsxYn67lFPnF5C524hIy6QE8O4ESTT+n2DXnA/xxfr5vQm5+8N9RVHu37mR3otV0w0tXO87Z3PL93l3bc7/nrrzm2RjtXOxeNzuC+J73nIP/7ji0IebLjnddTv+i1lRzSrCNY8wEON7Qnr3Wkgl2emM/83B/PecHfUvKgb9/oz7wcx4f+9JBlOuqJ/O3Wg1uTozev7NdO+9n/XvXLY7zu1Sx2Q+6K9XS0fe1TTz7qHp/cCWT78o1vqMCPffhWl3Hup49ZN+uWYglvfvCBzvyOUhT7uo9828/59sazP/vO737Y5w9+/8uHnerWufo7048uOcZ/+ec3HWdmoFZ/6Md9aFF6DUiAmbZ+37d00Ad8AAhCA7h/DwiBcRV/xHd+9EeBy3VGEqiBr/dvV1Fvwod/HrhlXVeCaedl/JWCFbiCnTdHNXh8iFd+EaR9z/eB0YdzL7hOAoiC/gdxOIh6vLWDQqh/SEheFsg/UIhgN8WEUVh4PueEjKZ4QoJNW0iAIYiAP/hqL+eCa4dyVQiCHYh1UqiEb3aFu7eENoeGaehjRkh9gCKGZjSHcncVdkaCbchsYLiH1vWG2tKCbIhzcZeAg1hjhdh8h4iIkKiIdsiID1aGgKchmJaGk7hnlQhYR6Z0M2iFEP+0V4LoifyWeppob3+nYQt4ilChiqLod6TIdZH4iknUicV2aJj2Rz14izChMYUli3BHfoQzjL/YRYe3P8eYiC8zYL6IjMk4NVjEjJLIchLTjI4oZinXX31TjX6oc9EVGT0jIpf4FsG4dSW2hbx4VriXeHmIi7WCRHhIjdA4hs74Y9pmj+4WhyDRYk6nMN+ohbliMSe2j97Wjw8hYvQYjgdpjfUYbr5HRQ45QmroDddlToDmaexXb624a5SYal4oDfSDSVsHPQJ5bbToayMHktJokRm1NuNlgluEfSkIkGbXkmImkgc1NlElZDCYknrUkcZoezmJDVlIiD2JfEAZaBv/+YdqJjzPOHGEt4jB4Ir09DfQxkJBCY7gJWvTlo+egZJagZRJaYdaZnft2IsUGZTFx3mv0DRjGU5XiZBAhY43eX5fqWo/0BRsCZN0aUOP4Y6RFTVGqYvLiFH3JR5VWWi26JI/Q1LSZSQCOYzlo4Pjl5aAGVTweJHFdTNvo1KU2ZI19CbbdjBquZNmWZZkOU9CxU2GaZiHeSiy5yePuZqWCIeqlFaQmZOhtpKxqJv+YZu32QfFCH3i1EfViJaD2VpFlpqaEJtkEZWqFZF8R5BX5ZdhWZqE9ZFCo42qqZnSKTuUU4r5JHVaWUJypYyKmXWF+ZLQGZ3iOZGJWHlPpZFt/5meiQmakblmBvmeaiWX8klhsDJh8fN0YOhq3rKdqiN+CdlSfvmXv6egXCcXXblb53aX42iXMhmSEMqajRQ2BRoWD6lAongLa3len+mgWKmKgSQRztWUBSd5lbla2ulEGIFlydBJ9rmUs1iiEAqcqGWdK8qiARqhD1d5UniWHvpvlNVWMfaffZGjLtpm+thjMxiglGho4fmhLUoOCZhvhIal8QmVU2VKujSlc+aCUIZ0/kemdYdWufhkZ8oOTGRP/eamTJoz6EmM34kI5OiPP3qnK5qnjHmA3SmnaPpXdUqURmqhJ0mmuAVr/TOSBYOLR2eKq8aJhnqCHIhUlkqlSP+qa76TqXfYkKUqfZ56Cqi6mYAamHlGnHmXiXqKdmVqOY4Jn3RSPyrKpU0XiG9Kk3c2ME/ZmLr6qqBEqwroncCqbnC6QKNqrBWJmr1qfrPJqaTEc50xnKKqk6z0nJu4rNe6ZRGmGQz0D9nxrb5qraz6F5xkO0p5rrGqrHhJrfw0hudoroxKrE/oW44aemMxbvrqpwE3mcw6e7CYr18qr0dIqpypV8YTsN06sMNUsOJ6e9ezo3O6r5jYr8naRkR6pFkpsen6f5VksJ1Xl/CqsSRbsg3qsCiLo7AksFFqgyZrsfukkDKrr+o1qDeLsx2asa+onz77s9Cqs9GoqsJar9j/krOKhLRNikFEa0RAS0y/+I9SO7VG67RIq54nizqXFm9Pm7ToaY4jAbZhK7aW6bH5ZxFiC7Xwt7AP+gxzuyaLobZYq6h0q7dti4pCird5u7eBW7UAu59/C7iCi7jRWq6Fy65Nm7iPS6eBOqGGu7KQa7mN67iM47Vae7mdO7GUcSAEurnx6rmlK6VquIQs46/SWrqWm6uK55XFMLqX2rqIy5P7GkWqu7YpW7vPMJeNlruyu7u827u+ELIPdKXCi6gaZIMYVrzcoKPaF7xP2pqNCqlbK4PPqyNF2KKJNb2iy0wqCb2WqL1fVZzHwKOL563TFW3M8Kfl6wk827uyqkPu//Sr6Aue8Du4bKa/ttpTHVuxtWBZ+ru/dNC//ju26Ua/DYttZetHBJyw8uu5ZJvA90lskHTB0wDBlFq5aCub/4uPs/pN7unAsbbBkWvAJcxVm9akcLq+m3q9XUfCIBuGJzy75paQ9uKN0GUgGHqqGzbDRWrDmNt/X/Zrwhis70iGP0x5GTzAQ7y0BRa7nvmHpOmLxinDQUfDJgzFLAtz4PpaaGTFnYbFF1pdn8uDXbzFCIhSyAZ/q8idmsecRwujxQrFcgt3Kpa+gBjHaTa+7SbEBPyh+IlMBOXAYyzHAsxY0QvBuvDC3TiteJqhfuwK4xoVBzzIb7uuyGp8iEzJfP8bQ5X6vMJQklfHnt87ZokMoirsxF1quqSMmXqmRmKsvKockO+JxkAxwYyMZztYoDSlu2RsvcbVwbZ7yUMnjr9Jm7X8ybfMyrnmyK4bquWXUgfansy8xDP2zCx8lMYsyg1MK2Xcs+BrywaKy17ciHq7sklcOzvMNcFczjuxQjNLt92szENrpowrmcIcv+2qsIJrz9S0kFlEX9gMw/0MZiNbz4e7crbsoy7sfgdtvhulpgD9zckZpx/0aPD8abYQSf/szccsmtcpmQNZwR9tySQq0DTLvRY9zR8sqOBMYBz9c9arXLSUy1Ic0rB8msfjsshLvfxszl1os9vsvI970RD/TdOfKkpLvalWRHJ1bJXSLNKRRhHB185XrM0lfMqdCblJrdLalcw9l9VCHcOEOtZxO3BIXdWb5MmIpqnoqtUiHLWLNdWXO81ZPGkgfMhlXc5hHVrN1Ll5TdQrXZP6nM0k3Qt3Pdi8nLu1CsazLL4C5r7RvMs8nZrweLd/vcAR7NhUzdhrvMIU61kwvMDyMb88TXzzGstrhM672ro6WrRx7ZFgk9Pb+tVdA3VXe80sDdu1q9vPzNvp9doxm9pCiM/ELdqEe9wvmNy4Oijly4TPHcVi4hGg/IDUrdbvGrZ2rYHavd3MTccw7arTB94bmzLyrNSgincbPbxRot4DicLh/+LelGs6BZy0830x0/jeMTLenZ2m0gJnq+vfEeuU3Ep0A37DbSC3ipuNCM6NgbfgXLjcM+HZj0rgEZKg/U06duzg6puo+cvF90vEm+fba7ioGG7foXvbfIyiL+tzoaCyo+ilIv7AwJXh3XfiKF7jLp7jN77jpjqCHP6xFX4f7E3bP17DRs6w01nipne7+u3jK87iRi1/Tg7jHsjkaRzgMqrkNQzkEl3dLBjlIU6wRB6GoGjWWX6xZf7l6jjmRWzlPsiKaF5OHj7hcI7e/FvckW3T0B0Rr4bnVA7YQS6CvVTlNW3nABSEAkrouQnoQE6EE+h4bz5Xu9eqiw7H4S1yo/8tccGp6Smtx44e52e+5yPO1Ia+3iIV6ueZZJnO5qw957qcmaUO6frV6s1adaR+6rfX4nNyjyet4xKa50m867yu6kkuwSWW31gY00/+4K/+u7Z+5eQbYlD6yJHOzhkN7V/HlDKu7UW+5VpO0VRWkNVEo7nuZCYH68kOcRLsjhkItezu5bln6Ta1x4M+7hTu7qbV0O93iPXpo3Za7JYsPK8765gI73LtdiPcpqOp7gee3wmPPCzW73RunboObqQ9Pj++nnyJ5HKuwRRv4mdMnZLsfDle2Y2Yq/nbvs6J8vUXn//uyf99FOD+vkNp2zGfqSjZ2ob8ovxbaEfN10Ouxab/PlKwWcoGZoTd7rbZpJUbCc0PnvRAWuv3DLzU/vQbD9HZSu/1/pTMuNcybYucvvX5zoFeH4NJPsVM18MqzmFnH1LMbuxWr6fHmNgSKdwkL/et3Ja1RTchKJd4D8BMz/d9/0hSzWg4pcTKbagjXdf22+eI3zl5lY3TEVaQnKw+v/Rjz8kmVR+Uj/FlCE21HZpvCvmptc+DJae/PoilKpXet6DXyvl+ffVrfvGM2PPcbtjjuba1z6YM72CPjoxF+ezv5q/fuDl8ZVXE/7QsyftiarBin6JhLEIR3/epKIyIuLpeyt+X+fmSnftP39shzHgEPqY6/NN656miX/Ia6vQH/+6FG75W1eeWqh/u7t+nhHz4BJAAU9faH8a4qJKnWsMenf6yMu0qzRNN1ZVt3ReO5TkcEYexbzrVOR7iS9QynR9m+DkqfUDnExqVTqlVZBG0sVm1wqowaaxIjqSgmTjirtlt9xsubp7VX7AVDGCG9/pr2q9kLo6w0PAQkWyQrkgqj+uRMVDwZ1LOUnIscZOz09NxizLUKRLvTkxlKU207tP1FTaWNQtQkyYPE9Sro4VvVnFUVniYmLPxZBGm1PQ0xhd459K2mLramu3YZPcFt21ZWZu2q/W63Pz8VnU2eKUb+1um+Sobvd7+XndIp1d+DZ51Xqlk+AgWNMiizD4U7v+8/YOGK8zAgxMpVgyYSVwfcu/61eon0WJIkfXUjaOHwyGzbes2NgA5EmbMYhmllUyp8qVLdhhLyvT581XPmrkC7my4UmM0j0KBNnVqaNqvpVHfMExKtCjTp1u5HtXahWXLqh0fYsrZFW1aKieVkR3rVhJKo2rp1n0yF+FNr2dN6uFrF3DgvGz5wXVjNSxhwYsZH0IMVW9fxY0pV96rMNHjsl8td/ZMKvJbpJuVfjZ9GopmyIYld8I68TVqOLFlqXasl3M82igL7Q7XzjfJ4NaezQw9G3fptcUZxWGe6rnc4bxST++hHB+aYbYR2ca+PHpf0dZ1hr842AgygIoKfx//C9q8a+63WR+On/s6Mu3Q3Usnj0QuSqSio73u+nNhP5c8mc/A+jjaDb+FDkzwugNNaisQ7xLEL8Lq4uMPKwvtO643Bv25j0LSFOtQOv1SDCdD1VLk8MOrIKKJG+ZeHHE0V0yExDwRpksJRwktFBJDyeaaEQ33NoztRgcrXCUzEumT0o4j9Rmyx/IwwJA2JHP040knw0uICGhgjBIv/9hkMYofq8QSvDB3qHFNKkVkIk8AEbxTx+deTOhNPHksdEecMCNGzikGjei/5oDRaUwoIW1rHDU3kMPFSRBNFLkbzow0SzrnNNVDS6MxVLkyY6y0Tz/5yZRPsGhdT0k2hYnO/9ATUT31rzpx/YM/TmuVFbj+zACVOmIlJXbHJPzq1dnaAmUWSCuNaTRVBY31EsQYQyxtz2/N8oXaFr8sT1wyURQPPWr2wzbbXzeBiBAN0G21xkq+a7Jcdm0FV11YZSVBX1arnVLe4tKtM9iguK20mXk/XPZf7ATF99aCP9BTyFVFDpgqQdZtWOPXSAWzS+O0pXiQa0XEOBeLVWZI5nGJSnjLkQUOdzh6731m45XbaxllpIG4cWE3Y0WW4G952oJCHInm2V2fOz5gnXDLuXpYRRc1h+MvpP65FlFQOdvjZrd+282a+QABa4Jp3vlTo+OxMW+hWY5414lnsLlTdea++v9mFB3WMiqEOah7YID5jfvlpfntG3BnKo9FcM0Rh3Cjw8kdNUjtEh3l8dQB1XrhJUaPMt+UMW/T8s1hKbvec4MLXSnJjZxQ8t2zwXr1rMVFm11PQdf7LovRwT27zv/c1zfjZfe49JIvDHAheNWlW1pI292DUhcHm/2slZk/X2myoXfU3+pZx/5w87XP4uL5BRY1/OLtXrx/RiLDVND3qCQdKnNJax8PxtAzeeCtd+LrhtU88CYcqIl//nOWBDXVuzR9jG8FVFbA2laqBZ7jfanSBNOeJr6sYKFojWDhb74EOceNr1YRlNsOmee3NSHqQfZyme0qNI0Zaipy1mvCCIf/l8IaQKt/x4MbFCnnxPOpsFBBTOA1rMhA1MFHee66YO2OsT5U2NCFU1yX6CyYDhL+bnpaHFtFuji4HOgoBAIM0BuT5cAB/rFYaGTd6XSohGPpho9PK1buTmiPI4LRjxN44g9pJ8nCZVBuk8wPFDV4sHdxb21so6EoEUmeh/VRiF+rI8yyxp5DDqUlZrrktDJGPgnRCnKeZFwoQVlCGD2La3bk4ymBNkeRrPJPnHSlNFR0EjNZSpkgZKZ+cBnF/+2yl1FD3iXpJ0xTJhKVW7wHMvMSTQJdQm18KuQr08OzSaHzlpw03jXtZAkAspMlyCNm30zYSIM8EpEb1AIv1eit/xwqrw+TQthAs9m0SbrzVhz8ZUOhVtC4tO2U/ISf9MZJzniaTnekLCJDB5a8h4KNoMzCoUR1qaqpkUgVII2U34hJSWP6BKA5ehv1RApHXQJzbTJNaf5+xlKWvrOKMJWp7v5DU3DO8qYyyWlh4Pa6MJIKoq0rWlXtKTroFHVkUnRoQ+8J1LBxdZjUqmkzJyNVj2qScPp8aku1WTUc8pJmDDPnDbfJtrL21D/4HOtZ9dg8jh5kqnqNa0TTRYujbhWtBbMrPZMY1oIK9ZyA7esUI6RRSPoTJoml5mapN89wepWxsiStVdVYxkhu0JrJWyxFCSkzQJxWV2QkIh3fGjWbXf/Voi+k5ffSaMjV6tC0GmlR/YiLXMNdz6yCHWyRXtpbm0Z1K7Arpy+RlVW++TaKrVwmWoFb0YtminTJVZJxCRpcJjGxXL9Fqht3GxLRAnKzsF1dWKRGvJK6jbPI/e8HSZpe8squjYTNr/cmGt1g6ha0QLlvekh5Q/HeFpgWHjBJ86th87K3Jkh1FQ9zSyUTl8WbPfBcfUNr3Qqn8VEZEzB6SegqcIHKtg4T6W9LLN0FM9hgoz1aKt3q4g7DeGbD4uuHTyzZ/eo1qNnT2Q4NWuXLSrlwKe7UkCP8FO1OD4IQ1HJqN6zgMi3SXAZGIm/+6zhFrvkh1Z0d+4gs4QnD+cj/RkPceEcq2H4h+MfTxXJgC/tmdopQLGxta2C+3Mf5UvSKFM4siB6tzQZv871hVtXlSEfpLct5zlDFLmMa3WdptrdAaPb0qSF9aE5POc4eyQSBfWo+S9b6s12mS6k/Ha/yHRJbo+t1siR9NvnieXsJ3Qyr5UrUMUO4zl3htYJHaelc4Wmd1BZQsekn4yeP1SZMycgzK2lYMwq33I3psaGFfGXtzTfG3xRlXmO9JB4XkVzX9SGYzM3itKwbZJjjL7pvQrxPPavT1441ofENZurGaa2y1bVgAH7QUKP42twxOBDPmb2hLDy4tuSyaKAtTlJPO80CD5tnEX5xXjjbaSaG/9Nguckj+h52MRXnbpZsfhRvQjPk6aRYqPSMctnAErv7robSk+lGYqcjoEQvJc4po/MHj+TcsznNPo1+9BAu2uthv13XxY70dJcd7Vei+mesnna3r8bIYW/72+l+mWjLZu511/tG4472vO8d8F68c+AJnmjCH/5vdy97ghHfeIevPe2Md/zkRW1ywEue8pk3+9kpj3nNZ97zn/862EWv99CXHtSGR33dT7/6zXPe9ajJYux/N3jam2b2t6/8xHUPmNz3fvejBr66fz98ffPe+F4ufvITo3jmH7P1z0+96qXPFY5XH9+2x35Mrr/92pPd+1jvfvi/33fyP2/85w++5f/Vv/T0t3/9woc/+pc///jL3/4K/Hv+p099/jPq/f6v/LRPALclAAvw/vAPARuk/hYw+8DPAUvk4CKQlSCQAuXIAi+QgCBPAyuQADuw/2APBG8u+kZwADPQBBNv/1Lw+MyPBUcP+V5Q4lZQBluQA2VQ5Wrw8UpQB+msAXsQBmMQCENQAV/Q5YYw136wBtEHCSFmAoGQCZtQbA5QAwtICn3lCC9QhK4QgfKmCq2QC4kuBwtwC8Mwdsow/xDNDNUuC70P0bJuDVWQ5arvDeNwQerw+d4QDu1Q8NSw9/RwD/kwCdsQ9ABREAHQEAsREAPxEPNBDxFvESOuEcfjEVkvEhm9cRKxcBH97hIxMRPtLhHZrhPn6hM/YRSZrsVO0RNLUQJVcddUURJZcexgMRb1zxVlMRVpERU5RxdnDhfprxd9kQGDsRZ/UZWIkfTEEBmF0RgLYhlvUNGesRibMXqk0RqvkRSpUfywkRtpURs9oxvDcRO/UfbE0Ry9kBzF7hzXERrTUS3YER530R0tIx7FcR41rx6R8R5pLx9HcR+Zrx/B8B/dMCBdcCD/sCDl8SC37xwXcg1P0SEjUiIhoAAAADs=";
//        byte[] imgFile = ESSGetBase64Decode(aaa);
//        List<String> ImageBase64List = CutImageUtil.cutImageToBase64(imgFile,2);
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Utils.ESSGetBase64Decode(
//                ImageBase64List.get(1)));
//        System.out.println(ImageBase64List.size());

//        File file = new File("C:/File/demo.txt");
//        String strParentDirectory = file.getParentFile().getName();
//        System.out.println("文件的上级目录为 : " + strParentDirectory);







    }



}
