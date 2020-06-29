package com.beth.infy.controller;

import com.beth.infy.domain.ConvertToXmlRequest;
import com.beth.infy.domain.PSAC20022ResponseTemplateMapping;
import com.beth.infy.util.CommonConstants;
import javassist.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.*;

@RestController
public class GenerateXmlController extends AbstractController {

    @PostMapping(value = "/api/v1/generateXmlMapping", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> generateTemplateMapping(@RequestBody ConvertToXmlRequest request) throws Exception {

        logger.info("Started generating xml template mapping file...");

         boolean valid = validateFile(request);

         if(!valid) {
             //send error respnse back
         }

        String outputFileName = null;

        if (StringUtils.isEmpty(request.getTemplateMappingLocation())) {

            if (request.getFileType().equalsIgnoreCase(CommonConstants.TEMPLATE_TYPE)) {
                outputFileName = CommonConstants.PS009_TEMPLATE_MAPPING_LOCATION;
            }
            if (null == outputFileName) {
                //default location
                outputFileName = CommonConstants.PS009_TEMPLATE_MAPPING_LOCATION;
            }

            request.setTemplateMappingLocation(outputFileName);
        }

        generateXmlTemplate(request);
        populateXmlTemplate(request.getMapping());
        PSAC20022ResponseTemplateMapping response = new PSAC20022ResponseTemplateMapping();
        response.setMessageText("XML Mapping Tempalte generate succussfully...");
        return ResponseEntity.ok(gson.toJson(response));
    }

    private void populateXmlTemplate(Map mappingTemplateList) {
    }

    private boolean validateFile(ConvertToXmlRequest request) {

        if (StringUtils.isEmpty(request.getMappingFields()) ) {
            return false;
        }

        if (StringUtils.isEmpty(request.getFileType())) {
            return false;
        }

        if (StringUtils.isEmpty(request.getSchemaFileName())) {
            return false;
        }

        if (StringUtils.isEmpty(request.getCustomerId())) {
            return false;
        }

        request.setMapping(convertMappingTemplateListToArray(request.getMappingFields()));
        //TODO - store mapping fields in DB. We need this to map when populating values. for now, storing in tempFileName

        writeToFile( CommonConstants.PS009_TEMPLATE_MAPPING_LOCATION+"TemplateMappingFields.txt", request.getMappingFields());

        return true;
    }


    private Map convertMappingTemplateListToArray(String mappingTemplates)  {
        Map<String, String> templateMapping = new HashMap<>();
        String str[] = mappingTemplates.split(",");
        List<String> splitList = new ArrayList<String>();
        splitList = Arrays.asList(str);
        for(String each: splitList) {
            String str1[] = each.split(":");
            templateMapping.put(str1[0], str1[1]);
        }
        return templateMapping;
    }


    private void generateXmlTemplate(ConvertToXmlRequest request) {

        Class clazz;
        try {
            List<String> methodList = new ArrayList<>();
            methodList.add(CommonConstants.RESOURCE_FOLDER+ "methods/100/createXml01.txt");
            methodList.add(CommonConstants.RESOURCE_FOLDER+ "methods/100/populateXmlData02.txt");
            methodList.add(CommonConstants.RESOURCE_FOLDER+ "methods/100/populateXmlData03.txt");

            clazz = generateClazz(FILE_UPLOAD_LOCATION+request.getTemplateName()+".class", request.getTemplateName(), methodList);

            /**** ranga 05/23 - from above either class loads if exists, or creates dynamcially.
             * once class exists, invoke the ncessary meethos dynamcally.
             * create a new instance of newly or existing class
             * invoke the methods.
             */
           // Object instance = clazz.newInstance();
            //invoke methods

         /*   (clazz.getDeclaredMethod(CommonConstants.TEMPLATE_CREATE_XML_METHOD_NAME, String.class))
                    .invoke(instance, "/home/ranga/sandbox/springboot/psac009/"+request.getSchemaFileName()); */
            //modify xml based on mapping fields
          /*  (clazz.getDeclaredMethod(CommonConstants.TEMPLATE_MODIFY_XML_METHOD_NAME,  String.class))
                    .invoke(instance, "/home/ranga/sandbox/springboot/psac009/"+request.getFileType()+".xml"); */
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
