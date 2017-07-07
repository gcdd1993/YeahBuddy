package cn.edu.xmu.yeahbuddy.web;

import cn.edu.xmu.yeahbuddy.domain.Team;
import cn.edu.xmu.yeahbuddy.domain.TeamReport;
import cn.edu.xmu.yeahbuddy.model.TeamDto;
import cn.edu.xmu.yeahbuddy.service.TeamReportService;
import cn.edu.xmu.yeahbuddy.service.TeamService;
import cn.edu.xmu.yeahbuddy.utils.ResourceNotFoundException;
import org.jetbrains.annotations.NonNls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@Controller
public class TeamController {

    @NonNls
    private static Log log=LogFactory.getLog(TeamController.class);

    private static TeamService teamService;

    private static TeamReportService teamReportService;

    @Autowired
    public TeamController(TeamService teamService,TeamReportService teamReportService){
        this.teamService=teamService;
        this.teamReportService=teamReportService;
    }

    @RequestMapping(value = "/team/{teamId:\\d+}",method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showInformation(@PathVariable int teamId, Model model){
        Optional<Team> team=teamService.findByteamId(teamId);
        if(team==null){
            throw new ResourceNotFoundException();
        }

        model.addAttribute("team",team);
        model.addAttribute("formAction","/team/"+teamId);
        return "team/information";
    }

    @RequestMapping(value = "/team/{teamId:\\d+}",method = {RequestMethod.POST,RequestMethod.PUT})
    public String updateInformation(@PathVariable int teamId,TeamDto teamdto){
        teamService.updateTeam(teamId,teamdto);
        return "redirect:team/"+teamId;
    }

    @RequestMapping(value = "/team/{teamId:\\d+}/reports",method = {RequestMethod.GET,RequestMethod.HEAD})
    public String showReports(@PathVariable int teamId,Model model){
        List<TeamReport> teamReports=teamReportService.findByteamId(teamId);
        model.addAttribute("teamReports",teamReports);
        model.addAttribute("formAction","/team/"+teamId+"/reports");
        return "team/reports";
    }

}