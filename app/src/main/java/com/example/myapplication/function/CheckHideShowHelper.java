package com.example.myapplication.function;

import com.example.myapplication.user_interface.forms.model.Field;
import com.example.myapplication.user_interface.forms.model.OptionModel;

import java.util.List;

public class CheckHideShowHelper {
    public static String DEBUG_TAG = CheckHideShowHelper.class.getSimpleName();

    private boolean isVlist;
    private List<Field> fieldsList;
    private int dlistArrayPosition;
    private List<Field> additionalFieldDataList;

    public void checkHideShow(String fieldId) {

        String value = "";
        String fID = "field" + fieldId;
        String inputValue = "";
        boolean isFieldFound = false;

        for (int i = 0; i < getFieldsList().size(); i++) {
            Field fieldObj = getFieldsList().get(i);
            if (fID.equals(fieldObj.getId())) {
                isFieldFound = true;
                if (fieldObj.getType().toLowerCase().equals("checkbox")) {
                    value = fieldObj.getValue();
                    break;
                } else {
                    inputValue = fieldObj.getValue();

                    List<OptionModel> options = fieldObj.getOptionsArrayList();
                    if(options.isEmpty()){
                        value = inputValue;
                        break;
                    }else{
                        for (int j = 0; j < options.size(); j++) {
                            String val = options.get(j).getId();
                            if (val.contains(inputValue)) {
                                String[] op = val.split(":");
                                value = op[0].replaceAll("['\\{\")]", "");
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(isFieldFound){
            if (value.contains(" --- ")) {
                String[] arr1 = value.split(" --- ");
                value = arr1[0].trim();
            }
            divhideshow(fieldId, value);
        }
    }

    private void divhideshow(String id, String value) {
        try {
            String idlist = "";
            for (int i = 0; i < getAdditionalFieldDataList().size(); i++) {
                Field field = getAdditionalFieldDataList().get(i);
                if (field.getName().equals("stateids")) {
                    idlist = field.getValue();
                }
            }

            String checkstateid = "0";
            for(int i=0; i < getFieldsList().size(); i++){
                Field fobj = getFieldsList().get(i);
                if(fobj.getId().equals("statefield")){
                    checkstateid = fobj.getValue();
                    break;
                }
            }

            String[] arr = idlist.split("@");
            for (int i = 0; i < arr.length; i++) {
                //e.g.c45689-true-c
                if (arr[i].indexOf("c" + id + "-" + value + "-c") > -1) {

                    if ((arr[i].length() - 3) > ("c" + id + "-" + value + "-c").length()) {
                        String checkedids = "/";
                        String checkedidshide = "/";
                        checkedids += id + "/";
                        String[] arr2 = arr[i].split("-c");
                        boolean doshow = true;

                        for (int j = 0; j < arr2.length; j++) {
                            String tmp = arr2[j].trim();

                            if (!tmp.equals("") && tmp.indexOf("-") > 0) {
                                if (tmp.indexOf('c') == 0) tmp = tmp.substring(1);

                                String[] arr3 = tmp.split("-");
                                if (checkedids.indexOf("/"+arr3[0]+"/") < 0) {
                                    for (int b = 0; b < getFieldsList().size(); b++) {
                                        Field bObj = getFieldsList().get(b);
                                        if (bObj.getId().matches("field" + arr3[0])) {

                                            if (bObj.getType().toLowerCase().matches("checkbox")) {

                                                if (bObj.getValue().matches("false") || bObj.getValue().isEmpty()) {
                                                    if (checkedidshide.indexOf("/" + arr3[0] + "/") < 0) checkedidshide = checkedidshide.concat(arr3[0] + "/") ;
                                                }
                                            } else if (!bObj.getType().toLowerCase().matches("checkbox") && !bObj.getValue().matches(arr3[1])) {
                                                if(checkedidshide.indexOf("/"+arr3[0]+"/") < 0 ) checkedidshide += arr3[0]+"/";
                                            } else {
                                                checkedids =  checkedids.concat(arr3[0] + "/");
                                                checkedidshide = checkedidshide.replace("/" + arr3[0] + "/", "/");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }// end of j for loop
                        if ("/" != checkedidshide) doshow = false;
                        if(doshow && checkstateid!="0" && (arr[i].indexOf("s")==0||arr[i].indexOf("-c-s")>-1)) if(arr[i].indexOf("s"+checkstateid+"s") < 0 && arr[i].indexOf("s0s") < 0) doshow = false;

                        if (doshow) {
                            for (int j = 0; j < getFieldsList().size(); j++) {
                                Field fObj = getFieldsList().get(j);

                                if (fObj.getStates().matches(arr[i]) && fObj.getType().equals("hidden")) {
                                    fObj.setType(fObj.getFieldType());
                                }
                            }
                          //  notifyAdapterWithPayLoad(j);
                        } else{
                            for (int j = 0; j < getFieldsList().size(); j++) {
                                Field fObj = getFieldsList().get(j);
                                if (fObj.getStates().matches(arr[i])
                                        && !fObj.getType().equals("hidden")) {
                                    fObj.setFieldType(fObj.getType());
                                    fObj.setType("hidden");
                                }
                            }
                        }
                    } else {
                        for (int j = 0; j < getFieldsList().size(); j++) {
                            Field fObj = getFieldsList().get(j);

                            if (fObj.getStates().matches(arr[i]) && fObj.getType().equals("hidden")) {
                                fObj.setType(fObj.getFieldType());
                            }
                        }
                    }
                } else if (arr[i].indexOf("c" + id + "-") > -1) {
                    for (int j = 0; j < getFieldsList().size(); j++) {
                        Field fObj = getFieldsList().get(j);

                        if (fObj.getStates().matches(arr[i]) && !fObj.getType().equals("hidden")) {
                            fObj.setFieldType(fObj.getType());
                            fObj.setType("hidden");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isVlist() {
        return isVlist;
    }

    public void setVlist(boolean vlist) {
        isVlist = vlist;
    }


    private List<Field> getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(List<Field> fieldsList) {
        this.fieldsList = fieldsList;
    }

    public int getDlistArrayPosition() {
        return dlistArrayPosition;
    }

    public void setDlistArrayPosition(int dlistArrayPosition) {
        this.dlistArrayPosition = dlistArrayPosition;
    }

    public void setAdditionalFieldDataList(List<Field> additionalFieldDataList) {
        this.additionalFieldDataList = additionalFieldDataList;
    }

    public List<Field> getAdditionalFieldDataList() {
        return additionalFieldDataList;
    }
}
