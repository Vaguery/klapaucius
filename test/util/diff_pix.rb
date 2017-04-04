# you'll need to install these:

require 'diffy'
require "mini_magick"
require 'posix-spawn'

MiniMagick.configure do |config|
  config.shell_api = "posix-spawn"
end

src = "test/util"
target = "test/util/pngs"

size = 1

yamls = Dir.glob(File.join(src,"yamls","*.txt"))

MiniMagick::Tool::Convert.new do |convert|
  convert.size "0x2500"
  convert << "xc:none"
  convert << "#{target}/output.png"
end

step = 0
yamls.each_cons(2) do |pair|
  puts step
  MiniMagick::Tool::Convert.new do |convert|
    convert.size "#{size}x2500"
    convert << "xc:none"
    y = 0
    Diffy::Diff.new(pair[0], pair[1],
      :source => 'files').each do |line|
      char = line[0..1]
      convert.fill case char
        when "+ "
          "green"
        when "- "
          "red"
        when "  "
          "white"
        else
          "black"
        end
      convert.draw "rectangle 0,#{y} #{size},#{y+size}"
      y += size
    end
    convert << "#{target}/new_line.png"
  end


  MiniMagick::Tool::Convert.new do |convert|
    convert << "#{target}/output.png"
    convert << "#{target}/new_line.png"
    convert.append.+("#{target}/output.png")
  end

  step += 1
end
