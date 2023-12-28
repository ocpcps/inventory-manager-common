/*
 * Copyright (C) 2022 Lucas Nishimura <lucas.nishimura@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.osstelecom.db.inventory.manager.jobs;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Esta classe representa as jobs que podem ser desencadeadas em função das
 * atualizações
 *
 * @author Lucas Nishimura
 * @created 14.12.2022
 */
public class DBJobInstance {

    private String jobId;
    private Date jobStarted = new Date();
    private Date jobEnded;
    private DbJobStage currentJobStage;
    private Map<String, DbJobStage> jobStages = new ConcurrentHashMap<>();

    public void startJob() {
        this.setJobStarted(new Date());
    }

    public void endJob() {
        if (this.jobStarted != null) {
            this.setJobEnded(new Date());
        }
    }

    public DBJobInstance(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the jobStarted
     */
    public Date getJobStarted() {
        return jobStarted;
    }

    /**
     * @param jobStarted the jobStarted to set
     */
    public void setJobStarted(Date jobStarted) {
        this.jobStarted = jobStarted;
    }

    /**
     * @return the currentJobStage
     */
    public DbJobStage getCurrentJobStage() {
        return currentJobStage;
    }

    /**
     * @param currentJobStage the currentJobStage to set
     */
    public void setCurrentJobStage(DbJobStage currentJobStage) {
        this.currentJobStage = currentJobStage;
    }

    /**
     * @return the jobStages
     */
    public Map<String, DbJobStage> getJobStages() {
        return jobStages;
    }

    /**
     * @param jobStages the jobStages to set
     */
    public void setJobStages(Map<String, DbJobStage> jobStages) {
        this.jobStages = jobStages;
    }

    /**
     * @return the jobEnded
     */
    public Date getJobEnded() {
        return jobEnded;
    }

    /**
     * @param jobEnded the jobEnded to set
     */
    public void setJobEnded(Date jobEnded) {
        this.jobEnded = jobEnded;
    }

    public DbJobStage createJobStage(String name, String description) {
        DbJobStage stage = new DbJobStage();
        stage.setStartDate(new Date());
        stage.setJobStageName(name);
        stage.setJobDescription(description);
        this.jobStages.put(stage.getJobStageId(), stage);
        return stage;
    }

    public DbJobStage endJobStage(DbJobStage stage) {

        stage = this.jobStages.remove(stage.getJobStageId());
        stage.setDoneDate(new Date());
        return stage;
    }

}
