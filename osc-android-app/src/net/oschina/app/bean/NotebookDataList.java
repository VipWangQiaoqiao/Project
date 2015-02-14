package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

public class NotebookDataList extends Entity implements
        ListEntity<NotebookData> {

    private List<NotebookData> list = new ArrayList<NotebookData>();

    @Override
    public List<NotebookData> getList() {
        return null;
    }

    public void setList(List<NotebookData> list) {
        this.list = list;
    }

}