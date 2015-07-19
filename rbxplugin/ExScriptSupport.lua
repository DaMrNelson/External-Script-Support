--[[

	DaMrNelson's External Script Support Plugin
	Hopefully I'll remember to link the github for the accompanying program here.

	TODO
		- Make setting the path actually work
		- X button for main frame

]]

local HttpService = game:GetService("HttpService")
local InputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")
local ServerStorage = game:GetService("ServerStorage")

local boxChecked = "rbxassetid://203550339"
local boxUnchecked = "rbxassetid://203550334"

local gui = script.Parent:WaitForChild("ESS")

local removeAllMain = gui:WaitForChild("RemoveAllConfirm")
local createMain = gui:WaitForChild("CreateDialog")

local body = gui:WaitForChild("Main"):WaitForChild("Body")
local main = body:WaitForChild("MainContent")
local listContainer = main:WaitForChild("List")
local list = listContainer:WaitForChild("DrawableArea")

local headerButtons = body:WaitForChild("HeadButtons")

local info = body:FindFirstChild("Info")
local idInput = info:WaitForChild("IDHolder"):WaitForChild("Input")
local enabledIcon = info:WaitForChild("EnabledIcon")
local pathButton = info:WaitForChild("SetPath")
local pathDisplay = info:WaitForChild("PathDisplay")
local iRemove = info:WaitForChild("Delete")

local links = {}
local currentLink

function rethinkList()
	list:ClearAllChildren()

	for i, link in ipairs(links) do
		local display = script.DisplayExample:clone()
		display.Name = "Display"

		display.Input.Text = link[1]
		display.Title.Text = link[2]:GetFullName()

		local function onFocus()
			if currentLink and currentLink[4] then
				currentLink[4].Hover.Visible = false
			end

			currentLink = link
			rethinkInfo()
		end

		display.Input.Focused:connect(onFocus)
		display.ActualButton.MouseButton1Click:connect(onFocus)

		display.Input.FocusLost:connect(function(wasEnter)
			if wasEnter then
				link[1] = display.Input.Text
				save()
				rethinkInfo()
			else
				display.Input.Text = link[1]
			end
		end)

		display.ActualButton.MouseEnter:connect(function()
			display.Hover.Visible = true
		end)

		display.ActualButton.MouseLeave:connect(function()
			if currentLink ~= link then
				display.Hover.Visible = false
			end
		end)

		display.Position = UDim2.new(0, 0, 0, 21 * (i - 1))
		display.Parent = list
		link[4] = display
	end

	list.Size = UDim2.new(1, 0, 0, #links * 21)
end

function save()
	local supportDir = ServerStorage:FindFirstChild("ExternalScriptSupport")

	if not supportDir then
		supportDir = Instance.new("Folder", ServerStorage)
		supportDir.Name = "ExternalScriptSupport"

		local readme = script.ReadMe_MainSupport:clone()
		readme.Name = "README"
		readme.Parent = supportDir
	end

	local linksDir = supportDir:FindFirstChild("Links")

	if not linksDir then
		linksDir = Instance.new("Folder", supportDir)
		linksDir.Name = "Links"
	end

	linksDir:ClearAllChildren()

	for i, link in ipairs(links) do
		local lk = Instance.new("Folder", linksDir)
		lk.Name = "Link"

		local id = Instance.new("StringValue", lk)
		id.Name = "ID"
		id.Value = link[1]

		local script = Instance.new("ObjectValue", lk)
		script.Name = "Script"
		script.Value = link[2]

		local enabled = Instance.new("BoolValue", lk)
		enabled.Name = "Enabled"
		enabled.Value = link[3]
	end
end

function load()
	links = {}

	local supportDir = ServerStorage:FindFirstChild("ExternalScriptSupport")

	if not supportDir then
		supportDir = Instance.new("Folder", ServerStorage)
		supportDir.Name = "ExternalScriptSupport"

		local readme = script.ReadMe_MainSupport:clone()
		readme.Name = "README"
		readme.Parent = supportDir
	end

	local linksDir = supportDir:FindFirstChild("Links")

	if not linksDir then
		linksDir = Instance.new("Folder", supportDir)
		linksDir.Name = "Links"
	end

	for i, link in ipairs(linksDir:GetChildren()) do
		if link:FindFirstChild("ID") and link:FindFirstChild("Script") and link:FindFirstChild("Enabled") then
			table.insert(links, {link.ID.Value, link.Script.Value, link.Enabled.Value})
		elseif link.Name == "Link" then
			link.Name = "[Broken]Link"
		end
	end
end

function rethinkInfo()
	if currentLink then
		idInput.Text = currentLink[1]
		pathDisplay.Text = currentLink[2] and currentLink[2]:GetFullName() or "None linked"
		enabledIcon.Image = currentLink[3] and boxChecked or boxUnchecked
	else
		idInput.Text = ""
		pathDisplay.Text = "None linked"
		enabledIcon.Image = boxUnchecked
	end
end

do -- Animate scroll bar
	local listScroll = listContainer:WaitForChild("ScrollBar")
	local centerB = listScroll:WaitForChild("CenterButtonContainer"):WaitForChild("CenterButton")
	local centerBB = listScroll:WaitForChild("CenterBackground")
	local topB = listScroll:WaitForChild("TopButton")
	local bottomB = listScroll:WaitForChild("BottomButton")

	local step = 21
	local y = 0 -- Pixels

	local function rethink()
		if list.AbsoluteSize.Y < listContainer.AbsoluteSize.Y then -- Too small
			y = 0
			centerB.Size = UDim2.new(1, 0, 1, 0)
			centerB.Position = UDim2.new(0, 0, 0, 0)
		else
			if y + list.AbsoluteSize.Y < listContainer.AbsoluteSize.Y then -- Too high (y too low)
				y = listContainer.AbsoluteSize.Y - list.AbsoluteSize.Y
			elseif y > 0 then -- Too low (y too high)
				y = 0
			end

			centerB.Size = UDim2.new(1, 0, math.max(20 / listContainer.AbsoluteSize.Y, listContainer.AbsoluteSize.Y / list.AbsoluteSize.Y), 0)
			centerB.Position = UDim2.new(0, 0, y / (listContainer.AbsoluteSize.Y - list.AbsoluteSize.Y) * (1 - centerB.Size.Y.Scale), 0)
		end

		list.Position = UDim2.new(0, 0, 0, y)
	end

	topB.MouseButton1Down:connect(function()
		local isDown = true

		local con
		con = InputService.InputEnded:connect(function(input)
			if input.UserInputType == Enum.UserInputType.MouseButton1 then
				isDown = false
				con:disconnect()
			end
		end)

		y = y + step
		rethink()
		wait(0.3)

		while isDown do
			y = y + step
			rethink()
			wait(0.1)
		end
	end)

	bottomB.MouseButton1Down:connect(function()
		local isDown = true

		local con
		con = InputService.InputEnded:connect(function(input)
			if input.UserInputType == Enum.UserInputType.MouseButton1 then
				isDown = false
				con:disconnect()
			end
		end)

		y = y - step
		rethink()
		wait(0.3)

		while isDown do
			y = y - step
			rethink()
			wait(0.1)
		end
	end)

	centerBB.MouseButton1Down:connect(function(gx, gy)
		local ly = gy - centerBB.AbsolutePosition.Y - 36 -- -36 because ROBLOX doesn't cut the top bar out of this calculation
		y = ly / centerBB.AbsoluteSize.Y * (listContainer.AbsoluteSize.Y - list.AbsoluteSize.Y)
		rethink()
	end)

	centerB.MouseButton1Down:connect(function(gx, gy)
		local ly = gy
		local con1, con2

		con1 = InputService.InputChanged:connect(function(input)
			if input.UserInputType == Enum.UserInputType.MouseMovement then
				y = y - (input.Position.Y - ly) / (listContainer.AbsoluteSize.Y / list.AbsoluteSize.Y)
				ly = input.Position.Y
				rethink()
			end
		end)

		con2 = InputService.InputEnded:connect(function(input)
			if input.UserInputType == Enum.UserInputType.MouseButton1 then
				con1:disconnect()
				con2:disconnect()
			end
		end)
	end)

	rethink()

	list.Changed:connect(function(prop)
		if prop == "AbsoluteSize" then
			rethink()
		end
	end)
end

do -- Header buttons
	headerButtons:WaitForChild("Add").MouseButton1Click:connect(function()
		createMain.Visible = true
	end)

	headerButtons:WaitForChild("Remove").MouseButton1Click:connect(function()
		for i, link in pairs(links) do
			if link == currentLink then
				table.remove(links, i)
				break
			end
		end

		save()
		currentLink = nil
		rethinkList()
		rethinkInfo()
	end)

	headerButtons:WaitForChild("RemoveAll").MouseButton1Click:connect(function()
		removeAllMain.Visible = true
	end)

	headerButtons:WaitForChild("Help").MouseButton1Click:connect(function()
		-- TODO: Display GUI
	end)
end

do -- Dialog animation
	local removeAllSection = removeAllMain:WaitForChild("Body"):WaitForChild("Section")
	
	removeAllMain:WaitForChild("Header"):WaitForChild("Close").MouseButton1Click:connect(function()
		removeAllMain.Visible = false
	end)
		
	removeAllSection:WaitForChild("Yes").MouseButton1Click:connect(function()
		links = {}
		currentLink = nil
		save()
		rethinkList()
		rethinkInfo()
		removeAllMain.Visible = false
	end)
	
	removeAllSection:WaitForChild("No").MouseButton1Click:connect(function()
		removeAllMain.Visible = false
	end)
	
	local createSection = createMain:WaitForChild("Body"):WaitForChild("Section")

	createMain:WaitForChild("Header"):WaitForChild("Close").MouseButton1Click:connect(function()
		createMain.Visible = false
	end)
		
	createSection:WaitForChild("Create").MouseButton1Click:connect(function()
		-- TODO
		createMain.Visible = false
	end)
	
	createSection:WaitForChild("Cancel").MouseButton1Click:connect(function()
		createMain.Visible = false
	end)
end

do -- Input buttons
	idInput.FocusLost:connect(function(wasEnter)
		if currentLink then
			if wasEnter then
				currentLink[1] = idInput.Text
				save()
			end

			rethinkInfo()
		end
	end)

	enabledIcon:WaitForChild("Back").MouseButton1Click:connect(function()
		if currentLink then
			currentLink[3] = not currentLink[3]
			save()
			rethinkInfo()
		end
	end)

	pathButton.MouseButton1Click:connect(function()
		if currentLink then
			print("Uh oh, I forgot to do this. Please message DaMrNelson so I can go do it.") -- TODO: This function
		end
	end)

	iRemove.MouseButton1Click:connect(function()
		if currentLink then
			for i, link in pairs(links) do
				if link == currentLink then
					table.remove(links, i)
					break
				end
			end

			save()
			currentLink = nil
			rethinkList()
			rethinkInfo()
		end
	end)
end

do -- Content providing
	local ContentProvider = game:GetService("ContentProvider")

	for i, asset in pairs({"208304111", "203754186", "203550339", "203550334"}) do -- TODO: Make sure all used assets are here
		ContentProvider:Preload("rbxassetid://" .. asset)
	end
end

load()
rethinkList()