package com.application.anant.smartattendancemanager.Model;

import java.util.Map;

public interface SubjectsFetched {

    void OnSubjectsFetched(Map<String, Object> map);

    void OnNameFetched(String name);
}
